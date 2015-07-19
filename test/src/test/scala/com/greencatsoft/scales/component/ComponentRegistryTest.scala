package com.greencatsoft.scales.component

import org.scalajs.dom.{ document, Element }

import com.greencatsoft.greenlight.TestSuite

object ComponentRegistryTest extends TestSuite {

  implicit val doc = document

  "ComponentRegistry.register[A]" might "create a component definition for the given type and register it" in {
  }

  It should "throw a MissingMetadataException when the specified class does not have a '@name' annotation" in {

    trait BadComponent extends Component[Element]

    A_[MissingMetadataException] should be_thrown_in {
      ComponentRegistry.register[BadComponent]
    }
  }

  It should "throw an InvalidMetadataException when the specified name is not valid per specification" in {

    @name("nodash")
    trait BadComponent extends Component[Element]

    @name("1337-name!")
    trait AnotherBadComponent extends Component[Element]

    An_[InvalidMetadataException] should be_thrown_in {
      ComponentRegistry.register[BadComponent]
    }

    An_[InvalidMetadataException] should be_thrown_in {
      ComponentRegistry.register[AnotherBadComponent]
    }
  }

  It should "throw an InvalidMetadataException when the specified component has a reserved name" in {

    @name("font-face")
    trait BadComponent extends Component[Element]

    @name("missing-glyph")
    trait AnotherBadComponent extends Component[Element]

    An_[InvalidMetadataException] should be_thrown_in {
      ComponentRegistry.register[BadComponent]
    }

    An_[InvalidMetadataException] should be_thrown_in {
      ComponentRegistry.register[AnotherBadComponent]
    }
  }
}