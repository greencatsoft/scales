package scales.component

import scala.scalajs.js
import scala.scalajs.js.UndefOr

import org.scalajs.dom.{ Element, Node }

import scales.component.internal.ComponentProxy
import scales.el.ExpressionContext

class ComponentContext[A <: Element](val component: Component[A])
  extends ExpressionContext {
  require(component != null, "Missing argument 'component'.")

  private def element: A = component.element

  override def parent: Option[ExpressionContext] = {
    val unwrap = ComponentProxy.unwrap[Component[_]] _

    def find(node: Node): Option[Component[_]] = node match {
      case e: Element => unwrap(e) match {
        case c @ Some(_) => c
        case None => Option(e.parentNode).map(find).flatten
      }
      case _ => None
    }

    Option(element.parentNode).map(find).flatten.map(_.context)
  }

  def get[T](name: String): Option[T] = {
    val value: UndefOr[Any] = element.asInstanceOf[js.Dynamic].selectDynamic(name)

    value.toOption map {
      case f: js.Function => f.bind(element).asInstanceOf[T]
      case v => v.asInstanceOf[T]
    }
  }
}