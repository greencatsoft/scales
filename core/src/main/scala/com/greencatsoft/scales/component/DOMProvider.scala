package com.greencatsoft.scales.component

import org.scalajs.dom.{ Document, Element, Node }

import com.greencatsoft.scales.dom.Template

import scalatags.JsDom.Tag

trait DOMProvider[A <: Element] extends LifecycleAware[A] {
  this: Component[A] =>

  def build(document: Document): Node

  override def onCreate(element: A) {
    super.onCreate(element)

    val document = element.ownerDocument
    val child = build(document)

    element.appendChild(child)
  }
}

trait TemplateDOMProvider[A <: Element] extends DOMProvider[A] {
  this: Component[A] =>

  def templateSelector: String

  override def build(document: Document): Node = {
    val template = document.querySelector(templateSelector).asInstanceOf[Template] ensuring (_ != null,
      s"Failed to find the specified template node '$templateSelector'.")

    document.importNode(template.content, true)
  }
}

trait ScalaTagsDOMProvider[A <: Element] extends DOMProvider[A] {
  this: Component[A] =>

  def template: Tag

  override def build(document: Document): Node = document.importNode(template.render, true)
}