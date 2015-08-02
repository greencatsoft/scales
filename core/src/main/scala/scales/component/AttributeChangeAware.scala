package scales.component

import org.scalajs.dom.Element

trait AttributeChangeAware[A <: Element] {
  this: Component[A] =>

  def onAttributeChange(name: String, oldValue: Any, newValue: Any, element: A): Unit = Unit
}