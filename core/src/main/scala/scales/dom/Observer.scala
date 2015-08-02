package scales.dom

import scala.scalajs.js

trait Observer extends js.Object {

  def observe[A](obj: A, callback: js.Function1[js.Array[ChangeEvent], Unit]): A = js.native

  def observe[A](obj: A, callback: js.Function1[js.Array[ChangeEvent], Unit], acceptList: js.Array[String]): A = js.native

  def unobserve[A](obj: A, callback: js.Function1[js.Array[ChangeEvent], Unit]): A = js.native

  def deliverChangeRecords(callback: js.Function1[js.Array[ChangeEvent], Unit]): Unit = js.native
}

trait ChangeEvent extends js.Object {

  val name: String = js.native

  val `object`: Any = js.native

  val `type`: String = js.native

  val oldValue: Any = js.native
}
