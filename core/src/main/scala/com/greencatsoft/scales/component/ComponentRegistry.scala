package com.greencatsoft.scales.component

import scala.scalajs.js

import org.scalajs.dom.{ Document, document }

import com.greencatsoft.scales.dom.ElementRegistrationOptions
import com.greencatsoft.scales.dom.ElementRegistry.asElementRegistry

object ComponentRegistry {

  def register[A <: Component[_]](implicit doc: Document = document) {
    val definition = metadata[A]
    val configuration = definition.define()

    val prototype = js.Object.create(definition.prototype, configuration)
    val options = ElementRegistrationOptions(Some(prototype), definition.parent)

    doc.registerElement(definition.name, options)
  }

  def metadata[A <: Component[_]]: ComponentDefinition[_, A] = ???
}