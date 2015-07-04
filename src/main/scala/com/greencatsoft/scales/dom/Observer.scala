package com.greencatsoft.scales.dom

import scala.language.implicitConversions

import scala.scalajs.js

trait Observer extends js.Object {

  def observe[A](obj: A, callback: js.Function1[js.Array[Change], Unit]): A = js.native

  def observe[A](obj: A, callback: js.Function1[js.Array[Change], Unit], acceptList: js.Array[String]): A = js.native

  def unobserve[A](obj: A, callback: js.Function1[js.Array[Change], Unit]): A = js.native

  def deliverChangeRecords(callback: js.Function1[js.Array[Change], Unit]): Unit = js.native
}

trait Change extends js.Object {

  val name: String = js.native

  val `object`: Any = js.native

  val `type`: String = js.native

  val oldValue: Any = js.native
}

object Observer {

  implicit def asObservable(obj: js.Object): Observer = obj.asInstanceOf[Observer]
}