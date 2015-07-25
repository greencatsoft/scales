package com.greencatsoft.scales.dom

import scala.scalajs.js

trait Polyfill extends js.Object {

  def ready: Boolean = js.native

  def useNative: Boolean = js.native

  def takeRecords(): Unit = js.native
}

object Polyfill extends js.GlobalScope {

  def CustomElements: Polyfill = js.native
}