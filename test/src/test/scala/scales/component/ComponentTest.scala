package scales.component

import scala.scalajs.js.annotation.JSExport

import org.scalajs.dom.document
import org.scalajs.dom.html.Div

import com.greencatsoft.greenlight.TestSuite

import scalatags.JsDom.all.h1
import scalatags.JsDom.implicits.stringFrag

import scales.dom.Polyfill.CustomElements

object ComponentTest extends TestSuite {

  implicit val doc = document

  "Component.element" should "return the host element for a custom web component" in {
    @name("component-test-1")
    class MyComponent extends Component[Div]

    val constructor = ComponentRegistry.register[MyComponent]

    val component = constructor()

    component.element should not be (empty)
    component.element.tagName should not be ("DIV")
  }

  It should "throw a IllegalStateException when referenced before the component is initialized" in {
    @name("component-test-2")
    class MyComponent extends Component[Div]

    A_[IllegalStateException] should be_thrown_in {
      (new MyComponent).element
    }
  }

  "Component.contentRoot" should "provide a host element with which the component can construct its contents" in {
    @name("component-test-3")
    class MyComponent extends Component[Div]

    val constructor = ComponentRegistry.register[MyComponent]

    val component = constructor()
    val parent = document.body.appendChild(component.element)

    component.contentRoot should not be (empty)

    val node = h1("My Component").render

    component.contentRoot.appendChild(node)

    parent.firstChild should be (node)
  }

  It should "throw a IllegalStateException when referenced before the component is initialized" in {
    @name("component-test-4")
    class MyComponent extends Component[Div]

    A_[IllegalStateException] should be_thrown_in {
      (new MyComponent).contentRoot
    }
  }

  "Component.context" should "provide an expression context for the component" in {
    @name("component-test-5")
    class MyComponent extends Component[Div] {

      @JSExport
      val property = "test"
    }

    val constructor = ComponentRegistry.register[MyComponent]
    val component = constructor()

    document.body.appendChild(component.element)

    CustomElements.takeRecords()

    val context = component.context

    context should not be (empty)
    context("property") should be (Some("test"))
  }

  It should "throw a IllegalStateException when referenced before the component is initialized" in {
    @name("component-test-6")
    class MyComponent extends Component[Div]

    A_[IllegalStateException] should be_thrown_in {
      (new MyComponent).context
    }
  }
}