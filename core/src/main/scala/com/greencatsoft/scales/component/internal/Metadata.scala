package com.greencatsoft.scales.component.internal

import scala.scalajs.js

private[component] trait Metadata {

  def define(prototype: js.Dynamic): js.Dynamic
}