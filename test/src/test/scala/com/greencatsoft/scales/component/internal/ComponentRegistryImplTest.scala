package com.greencatsoft.scales.component.internal

import scala.scalajs.js.annotation.{ JSExport, JSExportAll, JSName }

import org.scalajs.dom.Element
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.{ HTMLElement, HTMLInputElement }

import com.greencatsoft.greenlight.TestSuite
import com.greencatsoft.scales.component.{ Component, enumerable }

object ComponentRegistryImplTest extends TestSuite {

  import ComponentRegistryImplFixture._

  "ComponentRegistryImpl.getPrototype[A]" should "return a name of the prototype object for the specified component" in {
    class MyComponent extends Component[HTMLInputElement]

    val prototype = getPrototype[MyComponent]

    prototype should be (Some("HTMLInputElement"))
  }

  It should "resolve a type alias to its base name" in {
    class MyComponent extends Component[Div]

    val prototype = getPrototype[MyComponent]

    prototype should be (Some("HTMLDivElement"))
  }

  It should "find the closest parent which is under the 'org.scalajs.dom' package" in {
    class MyComponent extends Component[HTMLElement]

    val prototype = getPrototype[MyComponent]

    prototype should be (Some("HTMLElement"))
  }

  It should "be able to handle parametrized types which do not directly implement the Component trait" in {
    class BaseComponent[A <: Element] extends Component[A]

    class MyComponent extends BaseComponent[HTMLInputElement]

    val prototype = getPrototype[MyComponent]

    prototype should be (Some("HTMLInputElement"))
  }

  "ComponentRegistryImpl.getProperties[A]" should "return a collection of property definitions of the specified component" in {
    class MyComponent extends Component[Div] {

      @JSExport
      val propertyA = 1

      @JSExport
      @enumerable
      var propertyB = "test"
    }

    val properties = getProperties[MyComponent]().sortBy(_.name)

    properties.size should be (2)

    properties.headOption foreach { p =>
      p.name should be ("propertyA")
      p.readOnly should be (true)
      p.enumerable should be (false)
    }

    properties.lastOption foreach { p =>
      p.name should be ("propertyB")
      p.readOnly should be (false)
      p.enumerable should be (true)
    }
  }

  It should "ignore any non-public properties" in {
    class MyComponent extends Component[Div] {

      @JSExport
      val propertyA = 1

      @JSExport
      protected var propertyB = "test"
    }

    val properties = getProperties[MyComponent]().sortBy(_.name)

    properties.size should be (1)

    properties.headOption foreach { p =>
      p.name should be ("propertyA")
      p.readOnly should be (true)
      p.enumerable should be (false)
    }
  }

  It should "ignore properties without @JSExport annotation, if not otherwise exported" in {
    class MyComponent extends Component[Div] {

      val propertyA = 1

      @JSExport
      var propertyB = "test"
    }

    val properties = getProperties[MyComponent]().sortBy(_.name)

    properties.size should be (1)

    properties.headOption foreach { p =>
      p.name should be ("propertyB")
      p.readOnly should be (false)
      p.enumerable should be (false)
    }
  }

  It should "include properties without @JSExport annotation, if the enclosing class is annotated with @JSExportAll" in {
    @JSExportAll
    class MyComponent extends Component[Div] {

      val propertyA = 1

      @enumerable
      var propertyB = "test"
    }

    val properties = getProperties[MyComponent]().sortBy(_.name)

    properties.size should be (2)

    properties.headOption foreach { p =>
      p.name should be ("propertyA")
      p.readOnly should be (true)
      p.enumerable should be (false)
    }

    properties.lastOption foreach { p =>
      p.name should be ("propertyB")
      p.readOnly should be (false)
      p.enumerable should be (true)
    }
  }

  It should "include properties inherited from the ancestors of the specified type" in {
    class BaseComponent[A <: Element] extends Component[A] {
      @JSExport
      @enumerable
      val propertyA = 1

      @JSExport
      protected var propertyC = "test"
    }

    class MyComponent extends BaseComponent[HTMLInputElement] {
      @JSExport
      var propertyB = "test"

      val propertyD = false
    }

    val properties = getProperties[MyComponent]().sortBy(_.name)

    properties.size should be (2)

    properties.headOption foreach { p =>
      p.name should be ("propertyA")
      p.readOnly should be (true)
      p.enumerable should be (true)
    }

    properties.lastOption foreach { p =>
      p.name should be ("propertyB")
      p.readOnly should be (false)
      p.enumerable should be (false)
    }
  }

  It should "use a name specified in @JSName when the annotation is available" in {
    class MyComponent extends Component[Div] {

      @JSExport
      @JSName("propertyB")
      val propertyA = 1
    }

    val properties = getProperties[MyComponent]()

    properties.size should be (1)

    properties.headOption foreach { p =>
      p.name should be ("propertyB")
    }
  }

  "ComponentRegistryImpl.getMethods[A]" should "return a collection of method definitions of the specified component" in {
    class MyComponent extends Component[Div] {

      @JSExport
      def hello(name: String, quote: Boolean): String =
        if (quote) s"Hello, '$name!'" else s"Hello, $name!"

      @JSExport
      def world(): Unit = println("Method without arguments.")
    }

    val methods = getMethods[MyComponent]().sortBy(_.name)

    methods.size should be (2)

    methods.headOption foreach { m =>
      m.name should be ("hello")
      m.arguments should be (Seq("name", "quote"))
    }

    methods.lastOption foreach { m =>
      m.name should be ("world")
      m.arguments should be (empty)
    }
  }

  It might "ignore any non-public methods or accessors" in {
    class MyComponent extends Component[Div] {

      @JSExport
      def name: String = "My Component"

      @JSExport
      def name_=(n: String) {
        println("No, 'My Component' is a good enough name for a test!")
      }

      @JSExport
      var description: String = "It is cool!"

      @JSExport
      def hello(name: String, quote: Boolean): String =
        if (quote) s"Hello, '$name!'" else s"Hello, $name!"

      @JSExport
      protected def world(): Unit = println("Method without arguments.")
    }

    val methods = getMethods[MyComponent]().sortBy(_.name)

    methods.size should be (1)

    methods.headOption foreach { m =>
      m.name should be ("hello")
      m.arguments should be (Seq("name", "quote"))
    }
  }

  It should "ignore methods without @JSExport annotation, if not otherwise exported" in {
    class MyComponent extends Component[Div] {

      @JSExport
      def hello(name: String, quote: Boolean): String =
        if (quote) s"Hello, '$name!'" else s"Hello, $name!"

      def world(): Unit = println("Method without arguments.")
    }

    val methods = getMethods[MyComponent]().sortBy(_.name)

    methods.size should be (1)

    methods.headOption foreach { m =>
      m.name should be ("hello")
      m.arguments should be (Seq("name", "quote"))
    }
  }

  It might "include methods without @JSExport annotation, if the enclosing class is annotated with @JSExportAll" in {
    @JSExportAll
    class MyComponent extends Component[Div] {

      def hello(name: String, quote: Boolean): String =
        if (quote) s"Hello, '$name!'" else s"Hello, $name!"

      def world(): Unit = println("Method without arguments.")
    }

    val methods = getMethods[MyComponent]().sortBy(_.name)

    methods.size should be (2)

    methods.headOption foreach { m =>
      m.name should be ("hello")
      m.arguments should be (Seq("name", "quote"))
    }

    methods.lastOption foreach { m =>
      m.name should be ("world")
      m.arguments should be (empty)
    }
  }

  It should "include methods inherited from the ancestors of the specified type" in {
    class BaseComponent[A <: Element] extends Component[A] {

      @JSExport
      def hello(name: String, quote: Boolean): String =
        if (quote) s"Hello, '$name!'" else s"Hello, $name!"
    }

    class MyComponent extends BaseComponent[HTMLInputElement] {

      @JSExport
      def world(): Unit = println("Method without arguments.")
    }

    val methods = getMethods[MyComponent]().sortBy(_.name)

    methods.size should be (2)

    methods.headOption foreach { m =>
      m.name should be ("hello")
      m.arguments should be (Seq("name", "quote"))
    }

    methods.lastOption foreach { m =>
      m.name should be ("world")
      m.arguments should be (empty)
    }
  }

  It should "use a name specified in @JSName when the annotation is available" in {
    class MyComponent extends Component[Div] {

      @JSName("goodbye")
      @JSExport
      def hello(name: String, quote: Boolean): String =
        if (quote) s"Hello, '$name!'" else s"Hello, $name!"
    }

    val methods = getMethods[MyComponent]()

    methods.size should be (1)

    methods.headOption foreach { p =>
      p.name should be ("goodbye")
    }
  }
}