package com.greencatsoft.scales.component.internal

import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.{ Dictionary, PropertyDescriptor }

case class PropertyDefinition(
  name: String, readOnly: Boolean = false, enumerable: Boolean = false) extends Metadata {
  require(name != null && name.length > 0, "Missing argument 'name'.")

  override def define(prototype: js.Dynamic): js.Dynamic = {
    implicit def asObject(d: js.Dynamic): js.Object = d.asInstanceOf[js.Object]
    implicit def asDynamic(o: js.Object): js.Dynamic = o.asInstanceOf[js.Dynamic]

    val getter: js.ThisFunction0[ComponentProxy[_], Any] =
      (proxy: ComponentProxy[_]) =>
        proxy.component.asInstanceOf[js.Dynamic].selectDynamic(name)

    val definition = Dictionary[Any](
      "get" -> getter,
      "configurable" -> false,
      "enumerable" -> enumerable)

    if (!readOnly) {
      val setter: js.ThisFunction1[ComponentProxy[_], js.Any, Unit] =
        (proxy: ComponentProxy[_], value: js.Any) => {
          proxy.component.asInstanceOf[js.Dynamic].updateDynamic(name)(value)
        }: Unit

      definition("set") = setter
    }

    val descriptor = definition.asInstanceOf[PropertyDescriptor]

    js.Object.defineProperty(prototype, name, descriptor)
  }
}

