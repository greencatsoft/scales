package com.greencatsoft.scales.component.internal

import scala.scalajs.js
import scala.scalajs.js.{ Dictionary, undefined }

import com.greencatsoft.scales.component.Component

private[component] case class PropertyDefinition[A <: Component[_], B](
  name: String,
  getter: js.ThisFunction0[ComponentProxy[A], B],
  setter: Option[js.ThisFunction1[ComponentProxy[A], B, Unit]] = None,
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
