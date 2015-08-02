package scales.component.internal

import scala.scalajs.js
import scala.scalajs.js.UndefOr

import scales.component.Component

trait ComponentProxy[A <: Component[_]] extends js.Object {
  this: A =>

  var component: UndefOr[A] = js.native
}