package com.greencatsoft.scales.component.internal

import org.scalajs.dom.Element
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.{ HTMLElement, HTMLInputElement }

import com.greencatsoft.greenlight.TestSuite
import com.greencatsoft.scales.component.{ Component, name }

object ComponentDefinitionTest extends TestSuite {

  import ComponentDefinitionFixture._

  "ComponentDefinition[A]" might "return component metadata of the specified type" in {

    @name("my-component")
    trait MyComponent extends Component[Element]

    val definition = ComponentDefinition[MyComponent]

    definition.name should be ("my-component")
    definition.prototype should not be (empty)
    definition.tag should be (empty)
  }

  "ComponentDefinition.Macros.getPrototype[A]" should "return a name of the prototype object for the specified component" in {

    trait MyComponent extends Component[HTMLInputElement]

    val prototype = getPrototype[MyComponent]

    prototype should be (Some("HTMLInputElement"))
  }

  It should "resolve a type alias to its base name" in {

    trait MyComponent extends Component[Div]

    val prototype = getPrototype[MyComponent]

    prototype should be (Some("HTMLDivElement"))
  }

  It should "find the closest parent which is under the 'org.scalajs.dom' package" in {

    trait MyComponent extends Component[HTMLElement]

    val prototype = getPrototype[MyComponent]

    prototype should be (Some("HTMLElement"))
  }

  It should "be able to handle parametrized types which do not directly implement the Component trait" in {

    trait BaseComponent[A <: Element] extends Component[A]

    trait MyComponent extends BaseComponent[HTMLInputElement]

    val prototype = getPrototype[MyComponent]

    prototype should be (Some("HTMLInputElement"))
  }

  trait MyDiv extends Div
}