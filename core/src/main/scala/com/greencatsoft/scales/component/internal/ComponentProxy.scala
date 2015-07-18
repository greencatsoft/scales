package com.greencatsoft.scales.component.internal

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import com.greencatsoft.scales.component.Component

private[component] trait ComponentProxy[A <: Component[_]] extends js.Object {
  this: A =>

  var component: UndefOr[A] = js.native
}