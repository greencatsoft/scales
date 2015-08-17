package scales.component

import scala.util.Success
import org.scalajs.dom.{ Element, document }
import com.greencatsoft.greenlight.TestSuite
import scales.binding.{ Converter, ConverterNotFoundException }
import scales.dom.LowPriorityImplicits
import scala.scalajs.js.annotation.JSExport

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

    component.element.setAttribute("song", "Feed the Birds")
    component.name should be ("Feed the Birds")
  }

  It should "be able to be handle a method whose return type is Option[T]" in {
    @name("attribute-test-3")
    trait MyComponent extends Component[Element] {

      @attribute
      def song: Option[String]

      @attribute
      def title: Option[String]
    }

    val constructor = ComponentRegistry.register[MyComponent]
    val component = constructor()

    component.element.setAttribute("song", "Sixteen Going On Seventeen")
    component.song should be (Some("Sixteen Going On Seventeen"))
    component.title should be (None)
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

  It should "throw an IllegalArgumentException when the specified attribute does not exists and the return type is not Option[T]" in {
    @name("attribute-test-5")
    trait MyComponent extends Component[Element] {

      @attribute
      def unspecified: String

      @attribute
      def empty: String

      @attribute
      def blank: String
    }

    val constructor = ComponentRegistry.register[MyComponent]
    val component = constructor()

    val element = component.element

    element.setAttribute("empty", "")
    element.setAttribute("blank", "  ")

    An_[IllegalArgumentException] should be_thrown_in {
      component.unspecified
    }

    An_[IllegalArgumentException] should be_thrown_in {
      component.empty should be ("")
    }

    An_[IllegalArgumentException] should be_thrown_in {
      component.blank should be ("  ")
    }
  }

  It should "be able to inject value type attributes using the default converters" in {
    @name("attribute-test-6")
    trait MyComponent extends Component[Element] {

      @attribute
      def boolean: Boolean

      @attribute
      def int: Int

      @attribute
      def float: Float

      @attribute
      def long: Long

      @attribute
      def double: Double
    }

    val constructor = ComponentRegistry.register[MyComponent]
    val component = constructor()

    component.element.setAttribute("boolean", "true")
    component.element.setAttribute("int", "123")
    component.element.setAttribute("float", "123f")
    component.element.setAttribute("long", "123412341234")
    component.element.setAttribute("double", "123412341234d")

    component.boolean should be (true)
    component.int should be (123)
    component.float should be (123f)
    component.long should be (123412341234l)
    component.double should be (123412341234d)
  }

  It should "be able to inject optional value type attributes using the default converters" in {
    @name("attribute-test-7")
    trait MyComponent extends Component[Element] {

      @attribute
      def boolean: Option[Boolean]

      @attribute
      def int: Option[Int]

      @attribute
      def float: Option[Float]

      @attribute
      def long: Option[Long]

      @attribute
      def double: Option[Double]
    }

    val constructor = ComponentRegistry.register[MyComponent]
    val component = constructor()

    component.boolean should be (empty)
    component.int should be (empty)
    component.float should be (empty)
    component.long should be (empty)
    component.double should be (empty)

    component.element.setAttribute("boolean", "true")
    component.element.setAttribute("int", "123")
    component.element.setAttribute("float", "123f")
    component.element.setAttribute("long", "123412341234")
    component.element.setAttribute("double", "123412341234d")

    component.boolean should be (Some(true))
    component.int should be (Some(123))
    component.float should be (Some(123f))
    component.long should be (Some(123412341234l))
    component.double should be (Some(123412341234d))
  }

  It should "be able to convert attribute values using a custom converter" in {
    @name("attribute-test-8")
    trait MyComponent extends Component[Element] {

      implicit val converter = new Converter[Seq[Int]] {
        override def apply(name: String) = Success(name.split(",").map(_.toInt))
      }

      @attribute
      def numbers: Seq[Int]
    }

    val constructor = ComponentRegistry.register[MyComponent]
    val component = constructor()

    component.element.setAttribute("numbers", "1,2,3,4,5")

    component.numbers should be (Seq(1, 2, 3, 4, 5))
  }

  It should "throw ConverterNotFoundException when there's no suitable converter in the scope for the specified type" in {
    @name("attribute-test-9")
    trait MyComponent extends Component[Element] {

      @attribute
      def numbers: Seq[Int]
    }

    val constructor = ComponentRegistry.register[MyComponent]
    val component = constructor()

    component.element.setAttribute("numbers", "1,2,3,4,5")

    A_[ConverterNotFoundException] should be_thrown_in {
      component.numbers
    }
  }

  It should "be able to evaluate an expression and inject the result as the attribute value" in {
    @name("attribute-test-10")
    trait MyComponent extends Component[Element] {

      @JSExport
      val contextVariable = "abc"

      @attribute
      def value: String
    }

    val constructor = ComponentRegistry.register[MyComponent]
    val component = constructor()

    document.body.appendChild(component.element)

    component.element.setAttribute("value", "%{contextVariable}")

    component.value should be ("abc")
  }
}