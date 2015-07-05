package com.greencatsoft.scales.component

import scala.scalajs.js
import scala.scalajs.js.{ Dictionary, undefined }

case class PropertyDefinition[A](
  name: String,
  getter: js.Function0[A],
  setter: Option[js.Function1[A, Unit]] = None,
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
