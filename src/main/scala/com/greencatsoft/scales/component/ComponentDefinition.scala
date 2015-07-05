package com.greencatsoft.scales.component

import scala.reflect.ClassTag

import scala.scalajs.js
import scala.scalajs.js.Any.fromString
import scala.scalajs.js.Dictionary
import scala.scalajs.js.Dynamic.global

import org.scalajs.dom.Element

case class ComponentDefinition[A <: Element](
  name: String,
  parent: Option[String],
  properties: Seq[PropertyDefinition[_]])(implicit tag: ClassTag[A]) extends Metadata {

  require(name != null && name.length > 0, "Missing argument 'name'.")
  require(name.contains("-"), "Component name should contain a dash('-') character.")
  require(parent != null, "Missing argument 'parent'.")
  require(properties != null, "Missing argument 'properties'.")

  def prototype: js.Object = global(tag.getClass.getSimpleName).prototype.asInstanceOf[js.Object]

  override def define(definition: Dictionary[Any] = Dictionary[Any]()): Dictionary[Any] = properties match {
    case head :: tail => tail.foldLeft(head.define(defineCallbacks(definition)))((d, p) => p.define(d))
    case _ => defineCallbacks(definition)
  }

  def defineCallbacks(definition: Dictionary[Any]): Dictionary[Any] = {
    val createdCallback: js.ThisFunction0[ComponentProxy[_, _], Unit] =
      (proxy: ComponentProxy[_, _]) => proxy.component foreach {
        case l: LifecycleAware => l.onCreate()
        case _ =>
      }

    val attachedCallback: js.ThisFunction0[ComponentProxy[_, _], Unit] =
      (proxy: ComponentProxy[_, _]) => proxy.component foreach {
        case l: LifecycleAware => l.onAttach()
        case _ =>
      }

    val detachedCallback: js.ThisFunction0[ComponentProxy[_, _], Unit] =
      (proxy: ComponentProxy[_, _]) => proxy.component foreach {
        case l: LifecycleAware => l.onDetach()
        case _ =>
      }

    val attributeChangedCallback: js.ThisFunction3[ComponentProxy[_, _], String, Any, Any, Unit] =
      (proxy: ComponentProxy[_, _], name: String, oldValue: Any, newValue: Any) => proxy.component foreach {
        case l: AttributeChangeAware => l.onAttributeChange(name, oldValue, newValue)
        case _ =>
      }

    definition("createdCallback") = createdCallback
    definition("attachedCallback") = attachedCallback
    definition("detachedCallback") = detachedCallback
    definition("attributeChangedCallback") = attributeChangedCallback

    definition
  }
}