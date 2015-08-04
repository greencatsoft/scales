package scales.component

import org.scalajs.dom.{ Element, document }
import org.scalajs.dom.html.Div

import com.greencatsoft.greenlight.TestSuite

import scales.dom.Polyfill.CustomElements

object AttributeChangeAwareTest extends TestSuite {

  implicit val doc = document

  "AttributeChangeAware.onAttributeChange" should "be invoked when value of an attribute is changed" in {
    case class Change(name: String, oldValue: Any, newValue: Any, element: Element)

    var lastChange: Option[Change] = None

    @name("attribute-change-test-1")
    class HellaComponent extends Component[Div] with AttributeChangeAware[Div] {

      override def onAttributeChange(name: String, oldValue: Any, newValue: Any, element: Div) {
        super.onAttributeChange(name, oldValue, newValue, element)

        lastChange = Some(Change(name, oldValue, newValue, element))
      }
    }

    val component = ComponentRegistry.register[HellaComponent].apply()
    val element = component.element

    CustomElements.takeRecords()

    lastChange should be (empty)

    element.setAttribute("title", "Maxine")

    CustomElements.takeRecords()

    lastChange should not be (empty)

    lastChange foreach {
      case Change(name, _, newValue, elem) =>
        name should be ("title")
        newValue should be ("Maxine")
        elem should be (element)
    }

    element.setAttribute("title", "Max, never Maxine!")

    CustomElements.takeRecords()

    lastChange should not be (empty)

    lastChange foreach {
      case Change(name, oldValue, newValue, elem) =>
        name should be ("title")
        oldValue should be ("Maxine")
        newValue should be ("Max, never Maxine!")
        elem should be (element)
    }
  }
}