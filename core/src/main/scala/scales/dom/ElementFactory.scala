package scales.dom

import scala.scalajs.js

import org.scalajs.dom.{ Document, Element }

trait ElementFactory extends js.Object {
  this: Document =>

  def createElement(localName: String, typeExtension: String): Element = js.native

  def createElementNS(namespace: String, qualifiedName: String, typeExtension: String): Element = js.native
}