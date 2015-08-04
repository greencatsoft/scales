package scales.query

import org.scalajs.dom.{ Element, NodeSelector }
import org.scalajs.dom.html.Div

import com.greencatsoft.greenlight.TestSuite

import scalatags.JsDom.all.{ cls, div, h1, stringAttr }
import scalatags.JsDom.implicits.stringFrag

object ShadowNodeProviderTest extends TestSuite {

  "ShadowNodeProvider.contentRoot" should "create a shadow root from the inherited 'contentRoot' property" in {
    val fixture = new Fixture
    val provider = newProvider(fixture)

    val root = provider.contentRoot

    root appendChild {
      h1("Joni Mitchell").render
    }

    def query(elem: Element) = Option(elem.querySelector("h1"))

    query(root) should not be (empty)
    query(root) foreach {
      _.innerHTML should be ("Joni Mitchell")
    }

    query(fixture.parent) should be (empty)
  }

  It should "return an existing shadow root of the parent's 'contentRoot' element, if it was already created" in {
    val fixture = new Fixture
    val provider = newProvider(fixture)

    val root = provider.contentRoot

    root appendChild {
      h1("Big Yellow Taxi").render
    }

    def query(elem: Element) = Option(elem.querySelector("h1"))

    query(provider.contentRoot) should not be (empty)
    query(provider.contentRoot) foreach {
      _.innerHTML should be ("Big Yellow Taxi")
    }

    query(fixture.parent) should be (empty)
  }

  def newProvider(fixture: Fixture) =
    new TestNodeProvider(fixture.content) with ShadowNodeProvider[Div]

  class Fixture {
    val parent: Div = div(div()).render
    def content: Div = parent.firstChild.asInstanceOf[Div]
  }

  class TestNodeProvider[A <: NodeSelector](node: A) extends NodeProvider[A] {
    override def contentRoot = node
  }
}