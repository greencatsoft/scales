package com.greencatsoft.scales.component

trait AttributeChangeAware {
  this: Component[_] =>

  def onAttributeChange(name: String, oldValue: Any, newValue: Any): Unit = Unit
}