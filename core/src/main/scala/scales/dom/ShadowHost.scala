package scales.dom

import scala.scalajs.js

import org.scalajs.dom.{ Element, NodeList }

trait ShadowHost extends js.Object {
  this: Element =>

  def createShadowRoot(): ShadowRoot = js.native

  def getDestinationInsertionPoints(): NodeList = js.native

  def shadowRoot: ShadowRoot = js.native
}
