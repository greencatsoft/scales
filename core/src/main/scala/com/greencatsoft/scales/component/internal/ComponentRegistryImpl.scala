package com.greencatsoft.scales.component.internal

import scala.reflect.macros.blackbox.Context

import org.scalajs.dom.Document

import com.greencatsoft.scales.component.{ Component, name }
import com.greencatsoft.scales.macros.AnnotationUtils

private[component] object ComponentRegistryImpl {

  private val NamePattern = "^[a-z]-[a-z]$".r

  private val ReservedNames = Set("annotation-xml", "color-profile", "font-face", "font-face-src",
    "font-face-uri", "font-face-format", "font-face-name", "missing-glyph")

  def isValidName(name: String): Boolean = name match {
    case NamePattern(_*) => true
    case _ => false
  }

  def isReservedName(name: String): Boolean = ReservedNames.contains(name)

  def register[A](c: Context)(doc: c.Expr[Document])(implicit tag: c.WeakTypeTag[A]): c.Expr[Option[String]] = {
    import c.universe._

    val name = AnnotationUtils.getValue[A, name](c)
    ???
  }

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