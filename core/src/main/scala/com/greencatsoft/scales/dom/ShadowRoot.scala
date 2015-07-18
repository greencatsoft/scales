package com.greencatsoft.scales.dom

import scala.scalajs.js
import scala.scalajs.js.UndefOr

import org.scalajs.dom.{ DocumentFragment, Element, NodeList, Selection, StyleSheetList }

trait ShadowRoot extends DocumentFragment {

  def getElementById(elementId: String): Element = js.native

  def getElementsByName(elementName: String): NodeList = js.native

  def getElementsByClassName(tagname: String): NodeList = js.native

  def getElementsByTagName(tagname: String): NodeList = js.native

  def getElementsByTagNameNS(namespaceURI: String, localName: String): NodeList = js.native

  def getSelection(): UndefOr[Selection] = js.native

  def activeElement: UndefOr[Element] = js.native

  def host: Element = js.native

  def olderShadowRoot: UndefOr[ShadowRoot] = js.native

  def innerHTML: String = js.native

  def styleSheets: StyleSheetList = js.native
}