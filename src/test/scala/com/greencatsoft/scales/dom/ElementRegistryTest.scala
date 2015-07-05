package com.greencatsoft.scales.dom

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{ global, literal, newInstance }

import org.scalajs.dom.{ document, Node }

import com.greencatsoft.greenlight.TestSuite

import ElementRegistry.asElementRegistry

/**
 * Test suite for document.registerElement() API.
 * Only works with PhantomJS 2.0 for now.
 */
object ElementRegistryTest extends TestSuite {

  "Document.registerElement(name)" should "register a custom component with the given name" in {
    An_[Exception] should not be_thrown_in {
      document.registerElement("sc-test")
    }
  }

  It should "report an error when the given component name does not contain a dash" in {
    An_[Exception] should be_thrown_in {
      document.registerElement("illegal_name")
    }
  }

  It should "return a constructor which can be used to instantiate the defined component" in {
    val component = newInstance(document.registerElement("sc-test2"))()

    document.body.appendChild(component.asInstanceOf[Node])

    val components = document.getElementsByTagName("sc-test2")

    components.length should be (1)
    components.item(0) should be (component)
  }

  "Document.registerElement(name, options)" should "allow declaring component properties" in {
    var value = "test"
    var changed = false

    val getter: js.Function0[Any] = () => value
    val setter: js.Function1[String, Unit] = (v: String) => { changed = true; value = v }

    val prototype = js.Object.create(global.HTMLElement.prototype.asInstanceOf[js.Object], literal {
      "property" -> literal (
        "get" -> getter,
        "set" -> setter,
        "enumerable" -> true,
        "configurable" -> true)
    })

    val options = ElementRegistrationOptions(Some(prototype))

    val component = newInstance(document.registerElement("sc-test3", options))()

    val node = document.body.appendChild(component.asInstanceOf[Node]).asInstanceOf[js.Dynamic]

    node.property should be ("test")

    node.property = "test2"

    node.property should be ("test2")

    changed should be (true)
  }
}
