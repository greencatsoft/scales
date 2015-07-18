package com.greencatsoft.scales.component

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

import scala.scalajs.js

object MacroUtils {

  def getAnnotatedValue[A, B <: StaticAnnotation](): Option[String] = macro Impl.getAnnotatedValue[A, B]

  def getPrototype[A](): Option[String] = macro Impl.getPrototype[A]

  private object Impl {

    def getAnnotatedValue[A, B <: StaticAnnotation](c: Context)()(
      implicit targetTag: c.WeakTypeTag[A], annotationTag: c.WeakTypeTag[B]): c.Expr[Option[String]] = {
      import c.universe._

      def find(hierarchy: List[Symbol]): Option[String] = hierarchy match {
        case head :: tail =>
          val annotations = head.typeSignature.typeSymbol.annotations

          val arg = annotations.map(_.tree) collectFirst {
            case a if a.tpe =:= annotationTag.tpe => a.children.tail
          }

          arg collectFirst {
            case List(Literal(Constant(literal: String))) => literal
          } match {
            case Some(s) => Some(s)
            case _ => find(tail)
          }
        case _ => None
      }

      find(targetTag.tpe.baseClasses) match {
        case Some(value) =>
          c.Expr[Option[String]] {
            Apply(Select(Ident(TermName("Some")), TermName("apply")), List(Literal(Constant(value))))
          }
        case None => reify(None)
      }
    }

    def getPrototype[A](c: Context)()(implicit tag: c.WeakTypeTag[A]): c.Expr[Option[String]] = {
      import c.universe._

      val component = tag.tpe.baseClasses
        .map(_.asType)
        .filter(_.fullName == "com.greencatsoft.scales.component.Component")
        .map(_.toType)

      val types = component
        .map(_.member(TermName("element")))
        .flatMap(_.typeSignatureIn(tag.tpe).baseClasses)
        .map(_.asType)

      val name = types collectFirst {
        case tpe if tpe.fullName.startsWith("org.scalajs.dom") => tpe.name.toString
      }

      name match {
        case Some(value) =>
          c.Expr[Option[String]] {
            Apply(Select(Ident(TermName("Some")), TermName("apply")), List(Literal(Constant(value))))
          }
        case None => reify(None)
      }
    }
  }
}