package scales.component

import org.scalajs.dom.{ Element, document }
import org.scalajs.dom.html.Image

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

import com.greencatsoft.greenlight.TestSuite

import scales.dom.LowPriorityImplicits

object AttributeTest extends TestSuite with LowPriorityImplicits {

  implicit val doc = document

  "@attribute" should "instruct the registry to implement the annotated method to return the value of the matching attribute" in {
    @name("attribute-test-1")
    trait MyComponent extends Component[Element] {

      @attribute
      def title: String

      @attribute
      def song: String
    }

    val constructor = ComponentRegistry.register[MyComponent]
    val component = constructor()

    Option(component.title) should be (empty)
    Option(component.song) should be (empty)

    component.element.setAttribute("title", "Mary Poppins")
    component.title should be ("Mary Poppins")

    component.element.setAttribute("song", "Supercalifragilisticexpialidocious")
    component.song should be ("Supercalifragilisticexpialidocious")
  }

  It should "be able to be reference an attribute with a different name from the method by specifying 'name' argument" in {
    @name("attribute-test-2")
    trait MyComponent extends Component[Element] {

      @attribute("song")
      def name: String
    }

    val constructor = ComponentRegistry.register[MyComponent]
    val component = constructor()

    Option(component.name) should be (empty)

    component.element.setAttribute("song", "Feed the Birds")
    component.name should be ("Feed the Birds")
  }

  It should "be able to be handle a method whose return type is Option[T]" in {
    @name("attribute-test-3")
    trait MyComponent extends Component[Element] {

      @attribute
      def song: Option[String]
    }

    val constructor = ComponentRegistry.register[MyComponent]
    val component = constructor()

    component.song should be (empty)

    component.element.setAttribute("song", "Sixteen Going On Seventeen")
    component.song should be (Some("Sixteen Going On Seventeen"))
  }

  It should "be able to be handle a concrete method and treat the invocation result as a default value" in {
    @name("attribute-test-4")
    trait MyComponent extends Component[Element] {

      @attribute
      def song: String = "Wouldn't It Be Lovely"
    }

    val constructor = ComponentRegistry.register[MyComponent]
    val component = constructor()

    component.song should be ("Wouldn't It Be Lovely")

    component.element.setAttribute("song", "My Favorite Things")
    component.song should be ("My Favorite Things")
  }
}