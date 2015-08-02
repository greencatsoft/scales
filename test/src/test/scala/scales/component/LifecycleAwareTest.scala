package scales.component

import org.scalajs.dom.document
import org.scalajs.dom.html.Div

import com.greencatsoft.greenlight.TestSuite

import scales.dom.Polyfill.CustomElements

object LifecycleAwareTest extends TestSuite {

  implicit val doc = document

  "LifecycleAware.onCreate" should "be invoked when the component is initialized" in {
    var created = false
    var argument: Option[Div] = None

    @name("lifecycle-test-1")
    class MyComponent extends Component[Div] {

      override def onCreate(element: Div) {
        super.onCreate(element)

        created = true
        argument = Some(element)
      }
    }

    val constructor = ComponentRegistry.register[MyComponent]

    CustomElements.takeRecords()

    created should be (false)
    argument should be (empty)

    val component = constructor()

    CustomElements.takeRecords()

    created should be (true)
    argument should be (Some(component.element))
  }

  "LifecycleAware.onAttach" should "be invoked when the component is attached to a document" in {
    var attached = false
    var argument: Option[Div] = None

    @name("lifecycle-test-2")
    class MyComponent extends Component[Div] {

      override def onAttach(element: Div) {
        super.onAttach(element)

        attached = true
        argument = Some(element)
      }
    }

    val constructor = ComponentRegistry.register[MyComponent]
    val component = constructor()

    CustomElements.takeRecords()

    attached should be (false)
    argument should be (empty)

    document.body.appendChild(component.element)

    CustomElements.takeRecords()

    attached should be (true)
    argument should be (Some(component.element))
  }

  "LifecycleAware.onDetach" should "be invoked when the component is removed from its parent document" in {
    var detached = false
    var argument: Option[Div] = None

    @name("lifecycle-test-3")
    class MyComponent extends Component[Div] {

      override def onDetach(element: Div) {
        super.onDetach(element)

        detached = true
        argument = Some(element)
      }
    }

    val constructor = ComponentRegistry.register[MyComponent]
    val component = constructor()

    document.body.appendChild(component.element)

    CustomElements.takeRecords()

    detached should be (false)
    argument should be (empty)

    document.body.removeChild(component.element)

    CustomElements.takeRecords()

    detached should be (true)
    argument should be (Some(component.element))
  }
}