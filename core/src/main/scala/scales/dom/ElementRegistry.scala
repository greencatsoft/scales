package scales.dom

import scala.scalajs.js

import org.scalajs.dom.Document

trait ElementRegistry extends js.Object {
  this: Document =>

  def registerElement(name: String): js.Dynamic = js.native

  def registerElement(name: String, options: ElementRegistrationOptions): js.Dynamic = js.native
}
