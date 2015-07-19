package com.greencatsoft.scales.macros

import scala.annotation.StaticAnnotation
import scala.reflect.macros.blackbox.Context

object AnnotationUtils {

  def getValue[A, B <: StaticAnnotation](c: Context)()(
    implicit targetTag: c.WeakTypeTag[A], annotationTag: c.WeakTypeTag[B]): Option[String] = {
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

    find(targetTag.tpe.baseClasses)
  }

  def getValueExpr[A, B <: StaticAnnotation](c: Context)()(
    implicit targetTag: c.WeakTypeTag[A], annotationTag: c.WeakTypeTag[B]): c.Expr[Option[String]] = {
    import c.universe._

    getValue[A, B](c) match {
      case Some(value) =>
        c.Expr[Option[String]] {
          Apply(Select(Ident(TermName("Some")), TermName("apply")), List(Literal(Constant(value))))
        }
      case None => reify(None)
    }
  }
}