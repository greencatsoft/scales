package com.greencatsoft.scales.query

import org.scalajs.dom.{ Element, NodeSelector }

trait CachedNodeProvider[A <: NodeSelector] extends NodeProvider[A] {

  private var cache: Map[String, Seq[Element]] = Map.empty

  override def querySelector[T <: Element](selectors: String): Option[T] =     
    cache.get(selectors) match {
      case Some(result) => result.headOption.map(_.asInstanceOf[T])
      case None =>
        val result = super.querySelector[T](selectors)
        cache += (selectors -> result.toSeq)
        result
    }

  override def querySelectorAll[T <: Element](selectors: String): Seq[T] =
    cache.get(selectors) match {
      case Some(result) => result.map(_.asInstanceOf[T])
      case None =>
        val result = super.querySelectorAll[T](selectors)
        cache += (selectors -> result)
        result
    }

  def clearCache() {
    this.cache = Map.empty
  }
}