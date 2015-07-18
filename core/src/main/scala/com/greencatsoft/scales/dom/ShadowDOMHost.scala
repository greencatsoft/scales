package com.greencatsoft.scales.dom

import scala.scalajs.js
import scala.scalajs.js.UndefOr

import org.scalajs.dom.{ Element, NodeList }

trait ShadowDOMHost extends js.Object {
  this: Element =>

  def createShadowRoot(): ShadowRoot = js.native

  def getDestinationInsertionPoints(): NodeList = js.native

  def shadowRoot: UndefOr[ShadowRoot] = js.native
}
