package com.greencatsoft.scales.component.internal

import scala.scalajs.js
import scala.scalajs.js.{ Dictionary, undefined, UndefOr }
import scala.scalajs.js.Dynamic.global

import org.scalajs.dom.{ console, Element }

import com.greencatsoft.scales.component._
import com.greencatsoft.scales.di.{ Scope, ServiceFactory }

private[component] case class ComponentDefinition[A <: Component[_]](
  name: String,
  prototype: js.Object,
  tag: Option[String],
  properties: Seq[PropertyDefinition[A, _]]) extends Metadata {

  require(name != null, "Missing argument 'name'.")
  require(prototype != null, "Missing argument 'prototype'.")
  require(tag != null, "Missing argument 'tag'.")
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

private[component] object ComponentDefinition {

  private val NamePattern = "^[a-z]-[a-z]$".r

  private val ReservedNames = Set("annotation-xml", "color-profile", "font-face", "font-face-src",
    "font-face-uri", "font-face-format", "font-face-name", "missing-glyph")

  @throws[MissingMetadataException](
    "Thrown when the specified type does not have sufficient information to define a component.")
  @throws[InvalidMetadataException](
    "Thrown when the specified type does not have sufficient information to define a component.")
  def apply[A <: Component[_]]: ComponentDefinition[A] = {
    import MacroUtils._

    val name = getAnnotatedValue[A, name] getOrElse {
      throw new MissingMetadataException(
        "The specified component is missing @name annotation.")
    }

    if (!isValidName(name)) {
      throw new InvalidMetadataException(
        s"'$name' is not a valid name for a custom component.")
    }

    val tag = getAnnotatedValue[A, tag]

    val typeName = getAnnotatedValue[A, prototype] match {
      case v @ Some(_) => v
      case None => getPrototype[A]
    }

    val prototype = typeName
      .map(global(_))
      .map(_.asInstanceOf[UndefOr[js.Dynamic]])
      .map(_.toOption)
      .flatten
      .map(_.prototype) getOrElse {
        console.warn(
          "Failed to determine prototype object. Fallback to default ('HTMLElement.prototype').")
        global("HTMLElement").prototype
      }

    ComponentDefinition(name, prototype.asInstanceOf[js.Object], tag, Nil)
  }

  def isValidName(name: String): Boolean = name match {
    case NamePattern(_*) => true
    case _ => false
  }

  def isReservedName(name: String): Boolean = ReservedNames.contains(name)
}