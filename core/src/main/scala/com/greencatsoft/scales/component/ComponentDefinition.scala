package com.greencatsoft.scales.component

import scala.scalajs.js
import scala.scalajs.js.Dictionary

import org.scalajs.dom.Element

case class ComponentDefinition[A <: Element, B <: Component[A]](
  name: String,
  prototype: js.Object,
  parent: Option[String],
  properties: Seq[PropertyDefinition[A, B, _]]) extends Metadata {

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
    val createdCallback: js.ThisFunction0[ComponentProxy[A, _], Unit] =
      (proxy: ComponentProxy[A, _]) => proxy.component foreach {
        case l: LifecycleAware[A] => l.onCreate(proxy.asInstanceOf[A])
        case _ =>
      }

    val attachedCallback: js.ThisFunction0[ComponentProxy[A, _], Unit] =
      (proxy: ComponentProxy[A, _]) => proxy.component foreach {
        case l: LifecycleAware[A] => l.onAttach(proxy.asInstanceOf[A])
        case _ =>
      }

    val detachedCallback: js.ThisFunction0[ComponentProxy[A, _], Unit] =
      (proxy: ComponentProxy[A, _]) => proxy.component foreach {
        case l: LifecycleAware[A] => l.onDetach(proxy.asInstanceOf[A])
        case _ =>
      }

    val attributeChangedCallback: js.ThisFunction3[ComponentProxy[A, _], String, Any, Any, Unit] =
      (proxy: ComponentProxy[A, _], name: String, oldValue: Any, newValue: Any) =>
        proxy.component foreach {
          case l: AttributeChangeAware[A] =>
            l.onAttributeChange(name, oldValue, newValue, proxy.asInstanceOf[A])
          case _ =>
        }

    definition("createdCallback") = createdCallback
    definition("attachedCallback") = attachedCallback
    definition("detachedCallback") = detachedCallback
    definition("attributeChangedCallback") = attributeChangedCallback

    definition
  }
}