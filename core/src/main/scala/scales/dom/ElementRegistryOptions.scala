package scales.dom

import scala.scalajs.js

trait ElementRegistrationOptions extends js.Object {

  var prototype: js.UndefOr[js.Object] = js.native

  var `extends`: js.UndefOr[String] = js.native
}

object ElementRegistrationOptions {

  def apply(): ElementRegistrationOptions = (new Object).asInstanceOf[ElementRegistrationOptions]

  def apply(prototype: Option[js.Object] = None, parent: Option[String] = None): ElementRegistrationOptions = {
    val options: ElementRegistrationOptions = (new Object).asInstanceOf[ElementRegistrationOptions]

    prototype.foreach(options.prototype = _)
    parent.foreach(options.`extends` = _)

    options
  }
}
