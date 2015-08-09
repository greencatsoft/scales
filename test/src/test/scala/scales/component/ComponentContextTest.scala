package scales.component

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

import org.scalajs.dom.document
import org.scalajs.dom.html.Div

import com.greencatsoft.greenlight.TestSuite

import scales.dom.Polyfill.CustomElements

object ComponentContextTest extends TestSuite {

  implicit val doc = document

  "ComponentContext" should "be able to resolve exported properties of the component" in {
    @name("component-context-test-1")
    class MyComponent extends Component[Div] {

      @JSExport
      val string = "ABC"

      @JSExport
      val number = 123.45

      @JSExport
      val boolean = true

      @JSExport
      val option = Some("EFG")

      @JSExport
      val complex = this
    }

    val constructor = ComponentRegistry.register[MyComponent]
    val component = constructor()

    document.body.appendChild(component.element)

    CustomElements.takeRecords()

    val context = component.context

    context("string") should be (Some("ABC"))
    context("number") should be (Some(123.45))
    context("boolean") should be (Some(true))
    context("option") should be (Some(Some("EFG")))
    context("complex") should be (Some(component))
  }

  It should "delegate to its parent context when it fails to resolve the requested name in the current context" in {
    @name("component-context-test-2")
    class ParentComponent extends Component[Div] {

      @JSExport
      val property = "ABC"

      @JSExport
      val overriden = 123
    }

    @name("component-context-test-3")
    class ChildComponent extends Component[Div] {

      @JSExport
      val overriden = 456
    }

    val parent = ComponentRegistry.register[ParentComponent].apply()
    val child = ComponentRegistry.register[ChildComponent].apply()

    document.body.appendChild(parent.element)

    parent.element.appendChild(child.element)

    CustomElements.takeRecords()

    val context = child.context

    context("property") should be (Some("ABC"))
    context("overriden") should be (Some(456))
  }

  It should "return 'None' when it fails to resolve the given name in the current context hierarchy" in {
    @name("component-context-test-4")
    class MyComponent extends Component[Div]

    val constructor = ComponentRegistry.register[MyComponent]
    val component = constructor()

    document.body.appendChild(component.element)

    CustomElements.takeRecords()

    val context = component.context

    context("dummy") should be (empty)
  }

  "ComponentContext" should "be able to resolve exported methods of the component" in {
    @name("component-context-test-6")
    class MyComponent extends Component[Div] {

      var value: String = _

      @JSExport
      def write(v: String) = this.value = v

      @JSExport
      def read(): String = value
    }

    val constructor = ComponentRegistry.register[MyComponent]
    val component = constructor()

    document.body.appendChild(component.element)

    CustomElements.takeRecords()

    val context = component.context

    val reader = context[js.Function0[String]]("read")
    val writer = context[js.Function1[String, Unit]]("write")

    writer should not be (empty)
    writer.map(_.apply("test"))

    reader.map(_.apply) should be (Some("test"))
  }

  It should "delegate to its parent context when it fails to resolve the requested name in the current context" in {
    @name("component-context-test-7")
    class ParentComponent extends Component[Div] {

      var value: String = _

      @JSExport
      def write(v: String) = this.value = v

      @JSExport
      def read(): String = value
    }

    @name("component-context-test-8")
    class ChildComponent extends Component[Div] {

      @JSExport
      def read(): String = "abc"
    }

    val parent = ComponentRegistry.register[ParentComponent].apply()
    val child = ComponentRegistry.register[ChildComponent].apply()

    document.body.appendChild(parent.element)

    parent.element.appendChild(child.element)

    CustomElements.takeRecords()

    val context = child.context

    val reader = context[js.Function0[String]]("read")
    val writer = context[js.Function1[String, Unit]]("write")

    writer should not be (empty)
    writer.map(_.apply("test"))

    reader.map(_.apply) should be (Some("abc"))
    parent.value should be ("test")
  }
}