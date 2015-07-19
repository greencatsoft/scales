package com.greencatsoft.scales.component.internal

import org.scalajs.dom.Element

import com.greencatsoft.greenlight.TestSuite
import com.greencatsoft.scales.component.{ Component, name }

object ComponentDefinitionTest extends TestSuite {

  "ComponentDefinition[A]" might "return component metadata of the specified type" in {

    @name("my-component")
    trait MyComponent extends Component[Element]

    val definition = ComponentDefinition[MyComponent]

    definition.name should be ("my-component")
    definition.prototype should not be (empty)
    definition.tag should be (empty)
  }
}