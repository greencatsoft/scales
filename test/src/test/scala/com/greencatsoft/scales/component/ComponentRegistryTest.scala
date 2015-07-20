package com.greencatsoft.scales.component

import org.scalajs.dom.{ Element, document }

import com.greencatsoft.greenlight.TestSuite

object ComponentRegistryTest extends TestSuite {

  implicit val doc = document

  "ComponentRegistry.register[A]" should "create a component definition for the given type and register it" in {
    @name("my-component")
    class MyComponent extends Component[Element]

    val constructor = ComponentRegistry.register[MyComponent]

    constructor should not be (empty)
    constructor() should not be (empty)
  }

  It should "throw a MissingMetadataException when the specified class does not have a '@name' annotation" in {

    class BadComponent extends Component[Element]

    A_[MissingMetadataException] should be_thrown_in {
      ComponentRegistry.register[BadComponent]
    }
  }

  It should "throw an InvalidMetadataException when the specified name is not valid per specification" in {

    An_[InvalidMetadataException] should be_thrown_in {
      @name("nodash")
      class BadComponent extends Component[Element]

      ComponentRegistry.register[BadComponent]
    }

    An_[InvalidMetadataException] should be_thrown_in {
      @name("1337-name!")
      class AnotherBadComponent extends Component[Element]

      ComponentRegistry.register[AnotherBadComponent]
    }
  }

  It should "throw an InvalidMetadataException when the specified component has a reserved name" in {

    An_[InvalidMetadataException] should be_thrown_in {
      @name("font-face")
      class BadComponent extends Component[Element]

      ComponentRegistry.register[BadComponent]
    }

    An_[InvalidMetadataException] should be_thrown_in {
      @name("missing-glyph")
      class AnotherBadComponent extends Component[Element]

      ComponentRegistry.register[AnotherBadComponent]
    }
  }
}