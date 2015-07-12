package com.greencatsoft.scales.dom

import scala.language.implicitConversions

import scala.scalajs.js

import org.scalajs.dom.Document

trait ElementRegistry extends js.Object {

  def registerElement(name: String): js.Dynamic = js.native

  def registerElement(name: String, options: ElementRegistrationOptions): js.Dynamic = js.native
}

object ElementRegistry {

  implicit def asElementRegistry(document: Document): ElementRegistry = document.asInstanceOf[ElementRegistry]
}

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
