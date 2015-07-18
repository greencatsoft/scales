package com.greencatsoft.scales.component.internal

import scala.scalajs.js
import scala.scalajs.js.{ Dictionary, undefined }
import scala.scalajs.js.Dynamic.global

import org.scalajs.dom.Element

import com.greencatsoft.scales.component.{ AttributeChangeAware, Component, inherit, name }
import com.greencatsoft.scales.di.{ Scope, ServiceFactory }

private[component] case class ComponentDefinition[A <: Component[_]](
  name: String,
  prototype: js.Object,
  parent: Option[String],
  properties: Seq[PropertyDefinition[A, _]]) extends Metadata {

  require(name != null && name.length > 0, "Missing argument 'name'.")
  require(name.contains("-"), "Component name should contain a dash('-') character.")
  require(prototype != null, "Missing argument 'prototype'.")
  require(parent != null, "Missing argument 'parent'.")
  require(properties != null, "Missing argument 'properties'.")

  override def define(definition: Dictionary[Any] = Dictionary[Any]()): Dictionary[Any] = properties match {
    case head :: tail => tail.foldLeft(head.define(defineCallbacks(definition)))((d, p) => p.define(d))
    case _ => defineCallbacks(definition)
  }

  def defineCallbacks(definition: Dictionary[Any]): Dictionary[Any] = {
    val createdCallback: js.ThisFunction0[ComponentProxy[A], Unit] =
      (proxy: ComponentProxy[A]) => {
        val element = proxy.asInstanceOf[Element]
        val component = ServiceFactory.newInstance[A](Scope(element))

        proxy.component = component

        component.asInstanceOf[Component[Element]].onCreate(element)
      }

    val attachedCallback: js.ThisFunction0[ComponentProxy[A], Unit] =
      (proxy: ComponentProxy[A]) => proxy.component foreach { c =>
        val element = proxy.asInstanceOf[Element]
        val component = c.asInstanceOf[Component[Element]]

        component.onAttach(element)
      }

    val detachedCallback: js.ThisFunction0[ComponentProxy[A], Unit] =
      (proxy: ComponentProxy[A]) => proxy.component foreach { c =>
        val element = proxy.asInstanceOf[Element]
        val component = c.asInstanceOf[Component[Element]]

        proxy.component = undefined
      }

    val attributeChangedCallback: js.ThisFunction3[ComponentProxy[A], String, Any, Any, Unit] =
      (proxy: ComponentProxy[A], name: String, oldValue: Any, newValue: Any) =>
        proxy.component foreach {
          case l: AttributeChangeAware[_] =>
            val element = proxy.asInstanceOf[Element]
            val component = l.asInstanceOf[AttributeChangeAware[Element]]

            component.onAttributeChange(name, oldValue, newValue, element)
          case _ =>
        }

    definition("createdCallback") = createdCallback
    definition("attachedCallback") = attachedCallback
    definition("detachedCallback") = detachedCallback
    definition("attributeChangedCallback") = attributeChangedCallback

    definition
  }
}

object ComponentDefinition {

  def apply[A <: Component[_]]: ComponentDefinition[A] = {
    val name = MacroUtils.getAnnotatedValue[A, name]
    val inherit = MacroUtils.getAnnotatedValue[A, inherit]

    val prototype = MacroUtils.getPrototype[A].map(global(_).prototype)

    ???
  }
}