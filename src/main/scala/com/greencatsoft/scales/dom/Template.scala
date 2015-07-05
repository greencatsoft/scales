package com.greencatsoft.scales.dom

import scala.scalajs.js

import org.scalajs.dom.DocumentFragment
import org.scalajs.dom.html.Element

trait Template extends Element {

  val content: DocumentFragment = js.native
}