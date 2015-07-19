package com.greencatsoft.scales.component.internal

import scala.reflect.macros.blackbox.Context

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
  def apply[A <: Component[_]]: ComponentDefinition[A] = ???

  def isValidName(name: String): Boolean = name match {
    case NamePattern(_*) => true
    case _ => false
  }

  def isReservedName(name: String): Boolean = ReservedNames.contains(name)

  object Macros {

    def getPrototype[A <: Component[_]](c: Context)()(implicit tag: c.WeakTypeTag[A]): Option[String] = {
      import c.universe._

      val component = tag.tpe.baseClasses
        .map(_.asType)
        .filter(_.fullName == classOf[Component[_]].getName)
        .map(_.toType)

      val types = component
        .map(_.member(TermName("element")))
        .flatMap(_.typeSignatureIn(tag.tpe).baseClasses)
        .map(_.asType)

      types collectFirst {
        case tpe if tpe.fullName.startsWith("org.scalajs.dom") => tpe.name.toString
      }
    }

    def getPrototypeExpr[A <: Component[_]](c: Context)()(implicit tag: c.WeakTypeTag[A]): c.Expr[Option[String]] = {
      import c.universe._

      getPrototype[A](c) match {
        case Some(value) =>
          c.Expr[Option[String]] {
            Apply(Select(Ident(TermName("Some")), TermName("apply")), List(Literal(Constant(value))))
          }
        case None => reify(None)
      }
    }
  }
}