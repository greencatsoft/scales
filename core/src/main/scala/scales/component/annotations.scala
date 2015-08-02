package scales.component

import scala.annotation.StaticAnnotation

import org.scalajs.dom.Element

trait ComponentAnnotation extends StaticAnnotation

trait PropertyAnnotation extends StaticAnnotation

class name(name: String) extends ComponentAnnotation

class tag(name: String) extends ComponentAnnotation

class prototype(name: String) extends ComponentAnnotation

class enumerable extends PropertyAnnotation