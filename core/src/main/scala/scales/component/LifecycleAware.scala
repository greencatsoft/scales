package scales.component

import org.scalajs.dom.Element

trait LifecycleAware[A <: Element] {
  this: Component[_] =>

  def onCreate(element: A): Unit = Unit

  def onAttach(element: A): Unit = Unit
  
  def onDetach(element: A): Unit = Unit
}