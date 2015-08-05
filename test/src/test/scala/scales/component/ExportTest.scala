package scales.component

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

import org.scalajs.dom.{ Element, document }

import com.greencatsoft.greenlight.TestSuite

object ExportTest extends TestSuite {

  implicit val doc = document

  "Component" should "export properties annotated with @JSExport to the associated custom element" in {
    @name("component-properties-test-1")
    class MyComponent extends Component[Element] {

      @JSExport
      val readOnly = "A read-only property"

      @JSExport
      var writable = 10

      @JSExport
      @enumerable
      var enumerable = "An enumerable property"
    }

    val constructor = ComponentRegistry.register[MyComponent]

    val component = constructor()
    val element = component.element.asInstanceOf[js.Dynamic]

    element.readOnly should be ("A read-only property")

    An_[Exception] should be_thrown_in {
      element.readOnly = "Should throw an error."
    }

    element.writable should be (10)

    element.writable = 20
    element.writable should be (20)
    component.writable should be (20)

    component.writable = 30
    element.writable should be (30)

    val prototype = js.Object.getPrototypeOf(component.element)
    val properties = js.Object.keys(prototype).asInstanceOf[js.Array[String]].toSeq

    properties should not contain ("readOnly")
    properties should not contain ("writable")
    properties should contain ("enumerable")
  }

  "Component" should "export methods annotated with @JSExport to the associated custom element" in {
    @name("component-methods-test-1")
    class MyComponent extends Component[Element] {

      var greeted = false

      var arg1 = ""

      var arg2 = false

      var arg3 = 10

      @JSExport
      def hello() {
        greeted = true
      }

      @JSExport
      def test(arg1: String, arg2: Boolean, arg3: Int) {
        this.arg1 = arg1
        this.arg2 = arg2
        this.arg3 = arg3
      }
    }

    val constructor = ComponentRegistry.register[MyComponent]

    val component = constructor()
    val element = component.element.asInstanceOf[js.Dynamic]

    element.hello()

    component.greeted should be (true)

    element.test("test", true, 20)

    component.arg1 should be ("test")
    component.arg2 should be (true)
    component.arg3 should be (20)
  }
}