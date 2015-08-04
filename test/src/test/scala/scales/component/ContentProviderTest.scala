package scales.component

import org.scalajs.dom.{ Document, document }
import org.scalajs.dom.html.Div

import com.greencatsoft.greenlight.TestSuite

import scalatags.JsDom.all.h1
import scalatags.JsDom.implicits.stringFrag

object ContentProviderTest extends TestSuite {

  implicit val doc = document

  "ContentProvider" should "allow the implementing component to build its content" in {

    @name("content-provider-test-1")
    class MyComponent extends Component[Div] with ContentProvider[Div] {

      override def build(document: Document) = {
        val header = document.createElement("h1")

        header.innerHTML = "Harvey Girls"
        header
      }
    }

    val component = ComponentRegistry.register[MyComponent].apply()
    val element = component.element

    val header = Option(element.querySelector("h1"))

    header should not be (empty)
    header foreach {
      _.innerHTML should be ("Harvey Girls")
    }
  }

  "TemplateContentProvider" should "allow the component to build its content using an external template" in {

    @name("content-provider-test-2")
    class MyComponent extends Component[Div] with TemplateContentProvider[Div] {

      override def templateSelector: String = "#title"
    }

    val container = document.createElement("div")

    container.innerHTML = """
      <template id="title">
        <h1>For Me and My Gal</h1>
      </template>
    """

    document.body.appendChild(container)

    val component = ComponentRegistry.register[MyComponent].apply()
    val element = component.element

    document.body.appendChild(element)

    val header = Option(element.querySelector("h1"))

    header should not be (empty)

    header foreach {
      _.innerHTML should be ("For Me and My Gal")
    }
  }

  "ScalaTagsContentProvider" should "allow the component to build its content with ScalaTags markup" in {

    @name("content-provider-test-3")
    class MyComponent extends Component[Div] with ScalaTagsContentProvider[Div] {

      override def template = h1("Presenting Lily Mars")
    }

    val component = ComponentRegistry.register[MyComponent].apply()
    val element = component.element

    val header = Option(element.querySelector("h1"))

    header should not be (empty)
    header foreach {
      _.innerHTML should be ("Presenting Lily Mars")
    }
  }
}