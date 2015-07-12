package com.greencatsoft.scales.component

import scala.scalajs.js
import scala.scalajs.js.{ Dictionary, undefined }

import org.scalajs.dom.Element

case class PropertyDefinition[A <: Element, B <: Component[A], C](
  name: String,
  getter: js.ThisFunction0[ComponentProxy[A, B], C],
  setter: Option[js.ThisFunction1[ComponentProxy[A, B], C, Unit]] = None,
  enumerable: Boolean = false) extends Metadata {

  require(name != null && name.length > 0, "Missing argument 'name'.")
  require(getter != null, "Missing argument 'getter'.")
  require(setter != null, "Missing argument 'setter'.")

  override def define(definition: Dictionary[Any] = Dictionary[Any]()): Dictionary[Any] = {
    definition(name) = Dictionary[Any](
      "get" -> getter,
      "set" -> { setter getOrElse undefined },
      "configurable" -> false,
      "enumerable" -> enumerable)

    definition
  }
}
