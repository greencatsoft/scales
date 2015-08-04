package scales.query

import org.scalajs.dom.NodeSelector
import org.scalajs.dom.html.Heading

import com.greencatsoft.greenlight.TestSuite

import scalatags.JsDom.all.{ cls, div, h1, id, stringAttr }
import scalatags.JsDom.implicits.stringFrag

object CachedNodeProviderTest extends TestSuite {

  "CachedNodeProvider.querySelector()" should "return a matching element under the specified content root" in {
    val provider = newProvider()

    provider.querySelector(".the-seekers")
    provider.contentRoot.innerHTML = ""

    val heading: Option[Heading] = provider.querySelector(".the-seekers")

    heading should not be (empty)
    heading foreach {
      _.innerHTML should be ("Judith Durham")
    }
  }

  It should "return 'None' when there is no matching element for the specified query" in {
    val provider = newProvider()

    provider.querySelector(".jefferson-airplane")
    provider.contentRoot.innerHTML = ""

    provider.querySelector(".jefferson-airplane") should be (empty)
  }

  "CachedNodeProvider.querySelectorAll()" should "return all matching elements under the specified content root" in {
    val provider = newProvider()

    provider.querySelectorAll(".peter-paul-and-mary")
    provider.contentRoot.innerHTML = ""

    val headings: Seq[Heading] = provider.querySelectorAll(".peter-paul-and-mary")

    val titles = headings.map(_.innerHTML)

    titles.size should be (3)

    titles should contain ("Peter Yarrow")
    titles should contain ("Paul Stookey")
    titles should contain ("Mary Travers")
  }

  It should "return 'Nil' when there is no matching element for the specified query" in {
    val provider = newProvider()

    provider.querySelectorAll(".fifth-dimension")
    provider.contentRoot.innerHTML = ""

    provider.querySelectorAll(".fifth-dimension") should be (empty)
  }

  "CachedNodeProvider.clearCache()" should "clear all previously cached query results" in {
    val provider = newProvider()

    provider.querySelectorAll(".peter-paul-and-mary")
    provider.contentRoot.innerHTML = ""

    provider.clearCache()

    provider.querySelectorAll(".peter-paul-and-mary") should be (empty)
  }

  def newProvider() = {
    val fixture = div(id := "fixture")(
      h1(cls := "the-seekers")("Judith Durham"),
      h1 (cls := "peter-paul-and-mary")("Peter Yarrow"),
      h1 (cls := "peter-paul-and-mary")("Paul Stookey"),
      h1 (cls := "peter-paul-and-mary")("Mary Travers"))

    new TestNodeProvider(fixture.render)
  }

  class TestNodeProvider[A <: NodeSelector](val contentRoot: A) extends CachedNodeProvider[A]
}