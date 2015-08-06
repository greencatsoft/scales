Scales
================================

### Introduction

_Scales_ is a [_Web Components_](http://webcomponents.org/) based UI framework written in 
[_Scala.js_](http://www.scala-js.org/). It aims to provide a comprehensive and modular web 
framework which is entirely written in _Scala_, so that it could be used and extended easily 
by those developers who prefer the language to plain Javascript. 

### Current Status [![Build Status](http://ci.greencatsoft.com/buildStatus/icon?job=scales)](http://ci.greencatsoft.com/job/scales)

Currently, the project is in the _proof-of-concept_ stage, so it's not recommended to use it 
in a production environment. For implemented and planned features, please refer to the next 
section.

### Features

* Javascript wrapper for the _Web Components_ APIs.
* Statically typed custom component definition.
* A MVC framework (_planned_).
* Two way data binding of component attributes (_planned_).
* A dependency injection container for components and services (_planned_).
* Core UI components like widgets and layouts (_planned_).
* Services like Ajax, i18n, or routing support implemented as components (_planned_).

### How to Use

### SBT Settings

Add the following lines to your ```sbt``` build definition:

```scala
libraryDependencies += "com.greencatsoft" %%% "scales" % "0.1"
```

If you want to test the latest snapshot version instead, change the version to 
```0.2-SNAPSHOT``` and add Sonatype snapshot repository to the resolver as follows: 

```scala
resolvers += Resolver.sonatypeRepo("snapshots")
```

#### Defining a Component

To define a component, write a class which extends `Component[A]` trait and register it 
using `ComponentRegistry.register` method as below:

```scala
@name("my-component")
class MyComponent extends Component[Element]

ComponentRegistry.register[MyComponent]
```

And in a HTML file:
```html
<my-component />
```

Alternatively, you can use ```document.createElement``` method or a component constructor 
which is returned by the ```register``` method directly to create a new component instance.

```scala
ComponentRegistry.register[MyComponent]

val element = document.createElement("my-component")
document.body.appendChild(element)

// Or use a component constructor.
val consructor = ComponentRegistry.register[AnotherComponent]
val component = constructor()

document.body.appendChild(component.element)
```

When you define a component, it is required to specify a valid component name (which _must_ 
include a dash) with the ```@name``` annotation. Aside from this requirement, the component 
should be a concrete class with a public constructor without any arguments (it will be 
changed when the constructor based DI is implemented in future).

In case you want to extend a specific element prototype, you can change the type 
parameter accordingly, and optionally specify ```@tag``` with the name of the native 
tag you want to extend, like a below example: 

```scala
@name("fancy-button")
@tag("button") // Optional. Use this, if you want use the native tag(with 'is' attribute).
class FancyButton extends Component[Button]
```

In some cases, you might want your component to extend a custom prototype, which is not 
provided by [_Scala.js DOM_](https://github.com/scala-js/scala-js-dom) library.

In this case, you can use the ```@prototype``` annotation to specify the name of the prototype 
object you want to extend.  

```scala
@name("fancy-button")
@prototype("CustomButton")
class FancyButton extends Component[Button]
```

#### Properties and Methods

All public properties and methods with ```@JSExport``` annotation will be exported to the 
resulting custom element, and will be able to accessed from the Javascript side.

```scala
@name("hello-component")
class HelloComponent extends Component[Div] {

  @JSExport
  var greet: Boolean = false

  @JSExport
  var lastPerson: String = "(nobody)"

  @JSExport
  def hello(name: String) {
    if (greet) {
      console.log(s"Hello, $name!")
      lastPerson = name
    }
  }
}
```

And in Javascript:

```javascript
var component = document.getElementById("#greeter")

component.greet = true;
component.hello("Jane");

// Should print out "Jane".
console.log(component.lastPerson);
```

In Scala, you can also reference other custom components directly, like an example below:

```scala
val elem = document.getElementById("#greeter")
val component = elem.component.asInstanceOf[HelloComponent]

component.hello("Jane")

```

In future, components and services will be treated as dependencies so they can be resolved  
automatically in a declarative manner.

#### Lifecycle Callbacks

By default, ```Component``` extends the ```LifecycleAware``` trait, which provides various 
lifecycle callback methods defined by the _Web Components_ specification. Optionally, you 
can also mixin ```AttributeChangeAware```, if you want to be notified when the value of 
the component's attribute changes.

```scala
@name("hello-component")
class HelloComponent extends Component[Div] 
  with AttributeChangeAware[Div] {

  // Called when the component is created.
  override def onCreate(element: Div) {
    super.onCreated(element) // Don't forget to call super!
  }

  // Called when the component is attached to a DOM tree.
  override def onAttach(element: Div) {
    super.onAttach(element)
  }

  // Called when the component is detached from a DOM tree.
  override def onDetach(element: Div) {
    super.onDetach(element)
  }

  // Called when the value of an attribute is changed. 
  override def onAttributeChange(
    name: String, oldValue: Any, newValue: Any, element: Div) {
    onAttributeChange(name, oldValue, newValue, element)
    console.info(s"Attribute '$name' is changed: '$newValue'") 
  }
}
```

#### Defining Contents

By default, ```Component``` extends the ```NodeProvider``` trait, which means 
you can use its ```contentRoot``` property to build its content as below:

```scala
@name("hello-component")
class HelloComponent extends Component[Div] { 

  override def onCreate(element: Div) {
    super.onCreated(element)

    contentRoot.innerHTML = "<h1>Hello World!</h1>"
  }
}
```

Alternatively, you can make your component extend the ```ContentProvider``` trait, like 
a following example:

```scala
@name("hello-component")
class HelloComponent extends Component[Div] 
  with ContentProvider[Div] {

  override def build(document: Document): Node = {
    val elem = document.createElement("h1")
    elem.innerHTML = "Hello World!"
    elem
  }
}
```

There are other variants of the ```ContentProvider``` like ```TemplateContentProvider``` 
or ```ScalaTagsDOMProvider```, which can be mixed in when you want to use an external 
[_template_](http://webcomponents.org/articles/introduction-to-template-element/), 
or [ScalaTags](http://lihaoyi.github.io/scalatags/) to build the contents, respectively:

```scala
@name("templated-component")
class TemplatedComponent extends Component[Div] 
  with TemplateContentProvider[Div] {

  // Selector query for an external template tag.
  override def templateSelector = "#template"
}

@name("scalatags-component")
class ScalaTagsComponent extends Component[Div] 
  with ScalaTagsDOMProvider[Div] {

  override def template = h1("Hello World!")
}
```

### Requirements

* _Scala_ 2.11
* _Scala.js_ 0.6.4+
* A browser which supports _Web Components_ API, or [polyfills](http://webcomponents.org/polyfills/)
* [_PhantomJS_](http://phantomjs.org/) 2.0+ (for testing)

### License

This project is provided under the terms of [_Apache License, Version 2.0_](LICENSE).
