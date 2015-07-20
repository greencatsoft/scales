package com.greencatsoft.scales.component.internal

import scala.scalajs.js

import org.scalajs.dom.{ document, window }
import org.scalajs.dom.html.Element

import com.greencatsoft.greenlight.TestSuite

object ComponentDefinitionTest extends TestSuite {

  import ComponentDefinition._

  "ComponentDefinition.isValidName(name)" should "return true when the specified argument is a valid name for a custom element" in {
    isValidName("my-component") should be (true)

    isValidName("my-super-cool-component") should be (true)

    isValidName("UBER-COOL-COMPONENT") should be (true)

    isValidName("cool-component-2") should be (true)
  }

  It should "return false when the specified argument is not a valid name for a custom element" in {
    isValidName("nodash") should be (false)

    isValidName("$pecial-character$") should be (false)

    isValidName("another silly name") should be (false)
  }

  "ComponentDefinition.prototype(name)" should "return a JavaScript prototype for the the specified element type" in {
    val prototypeFor = (tagName: String) => js.Object.getPrototypeOf(document.createElement(tagName))

    prototype(Some("HTMLDivElement")) should be (prototypeFor("div"))
    prototype(Some("HTMLButtonElement")) should be (prototypeFor("button"))
  }

  It should "return HTMLElement.prototype when no name is given as the argument" in {
    val win = window.asInstanceOf[js.Dynamic]

    prototype(None) should be (win.HTMLElement.prototype)
  }
}