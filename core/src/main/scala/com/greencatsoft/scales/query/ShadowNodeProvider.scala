package com.greencatsoft.scales.query

import org.scalajs.dom.Element

import com.greencatsoft.scales.dom.ImplicitConversions.asShadowHost

trait ShadowNodeProvider[A <: Element] extends NodeProvider[A] {

  override abstract def contentRoot: A = {
    val element = super.contentRoot
    element.shadowRoot.getOrElse(element.createShadowRoot()).asInstanceOf[A]
  }
}