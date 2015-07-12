package com.greencatsoft.scales.component

import scala.scalajs.js.Dictionary

trait Metadata {

  def define(definition: Dictionary[Any] = Dictionary[Any]()): Dictionary[Any]
}