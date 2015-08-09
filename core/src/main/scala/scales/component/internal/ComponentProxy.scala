package scales.component.internal

import scala.scalajs.js
import scala.scalajs.js.UndefOr

import org.scalajs.dom.Element

import scales.component.Component

trait ComponentProxy[A <: Component[_]] extends js.Object {
  this: A =>

  var component: UndefOr[A] = js.native
}

object ComponentProxy {

  def unwrap[T <: Component[_]](element: Element): Option[T] =
    element.asInstanceOf[ComponentProxy[T]].component.toOption
}