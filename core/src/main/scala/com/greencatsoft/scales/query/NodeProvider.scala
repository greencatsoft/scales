package com.greencatsoft.scales.query

import scala.scalajs.js.UndefOr

import org.scalajs.dom.{ Element, Node, NodeSelector }
import org.scalajs.dom.ext.PimpedNodeList

trait NodeProvider[A <: NodeSelector] {

  def contentRoot: A

  def querySelector[T <: Element](selectors: String): Option[T] = {
    require(selectors != null, "Missing argument 'selectors'.")

    val result: UndefOr[Element] = contentRoot.querySelector(selectors)

    result.map(_.asInstanceOf[T]).toOption
  }

  def querySelectorAll[T <: Element](selectors: String): Seq[T] = {
    require(selectors != null, "Missing argument 'selectors'.")

    val result: Seq[Node] = contentRoot.querySelectorAll(selectors)

    result.map(_.asInstanceOf[T]).toSeq
  }
}