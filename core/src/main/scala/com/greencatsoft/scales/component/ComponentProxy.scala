package com.greencatsoft.scales.component

import scala.scalajs.js
import scala.scalajs.js.UndefOr

import org.scalajs.dom.Element

trait ComponentProxy[A <: Element, B <: Component[A]] extends js.Object {
  this: A =>

  def component: UndefOr[B] = js.native
}