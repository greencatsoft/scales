package com.greencatsoft.scales.component.internal

import scala.scalajs.js.Dictionary

private[component] trait Metadata {

  def define(definition: Dictionary[Any] = Dictionary[Any]()): Dictionary[Any]
}