package scales.component

import scala.scalajs.js
import scala.scalajs.js.UndefOr

import org.scalajs.dom.Element

import scales.el.ExpressionContext

class ComponentContext[A <: Element](
  val element: Element,
  val parent: Option[ExpressionContext]) extends ExpressionContext {
  require(element != null, "Missing argument 'element'.")
  require(parent != null, "Missing argument 'parent'.")

  def get[T](name: String): Option[T] = {
    val value: UndefOr[Any] = element.asInstanceOf[js.Dynamic].selectDynamic(name)

    value.toOption map {
      case f: js.Function => f.bind(element).asInstanceOf[T]
      case v => v.asInstanceOf[T]
    }
  }
}