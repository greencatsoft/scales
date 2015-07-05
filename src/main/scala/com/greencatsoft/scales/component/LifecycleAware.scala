package com.greencatsoft.scales.component

trait LifecycleAware {
  this: Component[_] =>

  def onCreate(): Unit = Unit

  def onAttach(): Unit = Unit
  
  def onDetach(): Unit = Unit
}