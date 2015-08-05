package scales.component

import org.scalajs.dom.{ Element, document }
import org.scalajs.dom.html.Image

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

import com.greencatsoft.greenlight.TestSuite

import scales.dom.LowPriorityImplicits

object ComponentRegistryTest extends TestSuite with LowPriorityImplicits {

  implicit val doc = document

  "ComponentRegistry.register[A]" should "register the given component type and return a factory instance associated with it" in {
    @name("component-registry-test-1")
    class MyComponent extends Component[Element]

    val constructor = ComponentRegistry.register[MyComponent]

    constructor should not be (empty)

    val component = constructor()

    component should not be (empty)
    component.isInstanceOf[MyComponent] should be (true)
  }

  It should "register the given component using a name which is specified by the @name annotation" in {
    var node: Option[Element] = None

    @name("component-registry-test-2")
    class MyComponent extends Component[Element] {

      override def onCreate(element: Element) {
        super.onCreate(element)

        node = Some(element)
      }
    }

    ComponentRegistry.register[MyComponent]

    val proxy = document.createElement("component-registry-test-2")

    node should not be (empty)
    node should be (Some(proxy))
  }

  It should "infer the component's prototype object from the given type parameter" in {
    @name("component-registry-test-3")
    class MyComponent extends Component[Image]

    val constructor = ComponentRegistry.register[MyComponent]
    val element = constructor().element

    element.tagName should not be ("IMG")
  }

  It should "use the information specified by @prototype over the type parameter, if the annotation is present" in {
    @name("component-registry-test-4")
    @prototype("HTMLImageElement")
    class MyComponent extends Component[Element]

    val constructor = ComponentRegistry.register[MyComponent]
    val element = constructor().element

    element.tagName should not be ("IMG")
  }

  It should "allow extending a native element by using @tag annotation" in {
    var node: Option[Element] = None

    @name("component-registry-test-5")
    @tag("img")
    class MyComponent extends Component[Image] {

      override def onCreate(element: Image) {
        super.onCreate(element)

        node = Some(element)
      }
    }

    ComponentRegistry.register[MyComponent]

    val element = document.createElement("img", "component-registry-test-5")

    node should not be (empty)
    node should be (Some(element))
  }

  It should "export properties defined in the specified type to the registered component" in {
    @name("component-registry-test-6")
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

  It should "export methods defined in the specified type to the registered component" in {
    @name("component-registry-test-7")
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

  It should "allow registering an abstract class as component definition" in {
    @name("component-registry-test-8")
    abstract class MyComponent extends Component[Element]

    val constructor = ComponentRegistry.register[MyComponent]

    constructor should not be (empty)

    val component = constructor()

    component should not be (empty)
  }

  It should "allow registering an abstract class as component definition" in {
    @name("component-registry-test-9")
    trait MyComponent extends Component[Element]

    val constructor = ComponentRegistry.register[MyComponent]

    constructor should not be (empty)

    val component = constructor()

    component should not be (empty)
  }

  It should "throw a MissingMetadataException when the specified class does not have a '@name' annotation" in {

    class BadComponent extends Component[Element]

    A_[MissingMetadataException] should be_thrown_in {
      ComponentRegistry.register[BadComponent]
    }
  }

  It should "throw an InvalidMetadataException when the specified name is not valid per specification" in {

    An_[InvalidMetadataException] should be_thrown_in {
      @name("nodash")
      class BadComponent extends Component[Element]

      ComponentRegistry.register[BadComponent]
    }

    An_[InvalidMetadataException] should be_thrown_in {
      @name("1337-name!")
      class AnotherBadComponent extends Component[Element]

      ComponentRegistry.register[AnotherBadComponent]
    }
  }

  It should "throw an InvalidMetadataException when the specified component has a reserved name" in {

    An_[InvalidMetadataException] should be_thrown_in {
      @name("font-face")
      class BadComponent extends Component[Element]

      ComponentRegistry.register[BadComponent]
    }

    An_[InvalidMetadataException] should be_thrown_in {
      @name("missing-glyph")
      class AnotherBadComponent extends Component[Element]

      ComponentRegistry.register[AnotherBadComponent]
    }
  }
}