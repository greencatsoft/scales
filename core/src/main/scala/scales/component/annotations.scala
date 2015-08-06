package scales.component

import scala.annotation.StaticAnnotation

import org.scalajs.dom.Element

trait ComponentAnnotation extends StaticAnnotation

trait PropertyAnnotation extends StaticAnnotation

class name(name: String) extends ComponentAnnotation

class tag(name: String) extends ComponentAnnotation

class prototype(name: String) extends ComponentAnnotation

class attribute extends PropertyAnnotation {

  private var _name: Option[String] = None

  def this(name: String) {
    this()

    this._name = Some(name)
  }

  def name: Option[String] = _name
}

class enumerable extends PropertyAnnotation