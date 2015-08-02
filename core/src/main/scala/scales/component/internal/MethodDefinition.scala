package scales.component.internal

import scala.language.implicitConversions
import scala.scalajs.js

case class MethodDefinition (
  name: String, arguments: Seq[String]) extends Metadata {
  require(name != null && name.length > 0, "Missing argument 'name'.")
  require(arguments != null, "Missing argument 'arguments'.")

  override def define(prototype: js.Dynamic): js.Dynamic = {
    val definition = s"""return this.component["$name"].apply(this.component, arguments)"""

    prototype.updateDynamic(name)(js.Function((arguments :+ definition): _*))
    prototype
  }
}

