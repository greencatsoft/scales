package com.greencatsoft.scales.component

import org.scalajs.dom.{ Element, Node }

import scalatags.JsDom.Tag

import com.greencatsoft.scales.dom.Template

trait DOMProvider[A <: Element] extends LifecycleAware {
  this: Component[A] =>

  def build(): Node

  override def onCreate() {
    super.onCreate()

    node.appendChild(build)
  }
}

trait TemplateDOMProvider[A <: Element] extends DOMProvider[A] {
  this: Component[A] =>

  def templateSelector: String

  override def build(): Node = {
    val doc = node.ownerDocument
    val template = doc.querySelector(templateSelector).asInstanceOf[Template] ensuring (_ != null,
      s"Failed to find the specified template node '$templateSelector'.")

    doc.importNode(template.content, true)
  }
}

trait ScalaTagsDOMProvider[A <: Element] extends DOMProvider[A] {
  this: Component[A] =>

  def template: Tag

  override def build(): Node = node.ownerDocument.importNode(template.render, true)
}