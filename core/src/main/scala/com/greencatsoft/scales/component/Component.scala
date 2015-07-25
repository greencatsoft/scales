package com.greencatsoft.scales.component

import org.scalajs.dom.Element

import com.greencatsoft.scales.query.NodeProvider

trait Component[A <: Element] extends NodeProvider[Element] with LifecycleAware[A] {

  private var _element: Option[A] = None

  @throws[IllegalStateException](
    "Thrown when invoked before the component is initialized.")
  def element: A = _element getOrElse {
    throw new IllegalStateException("The component has not been initialized yet.")
  }

  @throws[IllegalStateException](
    "Thrown when invoked before the component is initialized.")
  override def contentRoot: Element = element

  override def onCreate(element: A): Unit = {
    super.onCreate(element)

    this._element = Some(element)
  }
}