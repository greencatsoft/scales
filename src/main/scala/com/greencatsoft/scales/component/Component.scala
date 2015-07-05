package com.greencatsoft.scales.component

import org.scalajs.dom.Element

trait Component[A <: Element] {

  val node: A
}