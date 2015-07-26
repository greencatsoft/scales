package com.greencatsoft.scales.macros

import scala.annotation.StaticAnnotation
import scala.reflect.macros.blackbox.Context

private[scales] object AnnotationUtils {

  def getValue[A <: StaticAnnotation](c: Context)(target: c.WeakTypeTag[_])(implicit tag: c.WeakTypeTag[A]): Option[String] = {
    import c.universe._

    def find(hierarchy: List[Symbol]): Option[String] = hierarchy match {
      case head :: tail =>
        val annotations = head.typeSignature.typeSymbol.annotations

        val arg = annotations.map(_.tree) collectFirst {
          case a if a.tpe =:= tag.tpe => a.children.tail
        }

        arg collectFirst {
          case List(Literal(Constant(literal: String))) => literal
        } match {
          case s @ Some(_) => s
          case _ => find(tail)
        }
      case _ => None
    }

    find(target.tpe.baseClasses)
  }

  def getValueExpr[A <: StaticAnnotation](c: Context)(target: c.WeakTypeTag[_])(implicit tag: c.WeakTypeTag[A]): c.Expr[Option[String]] = {
    import c.universe._

    getValue[A](c)(target) match {
      case Some(value) =>
        c.Expr[Option[String]] {
          Apply(Select(Ident(TermName("Some")), TermName("apply")), List(Literal(Constant(value))))
        }
      case None => reify(None)
    }
  }

  def getMemberAnnotation[A <: StaticAnnotation](c: Context)(method: c.universe.MethodSymbol)(implicit tag: c.WeakTypeTag[A]): Option[String] = {
    import c.universe._

    val m = method match {
      case m if m.isAccessor => m.accessed
      case m => m
    }

    val arg = m.annotations collectFirst {
      case a if a.tree.tpe =:= tag.tpe => a.tree.children.tail
    }

    arg collectFirst {
      case List(Literal(Constant(literal: String))) => literal
    }
  }

  def hasMemberAnnotation[A <: StaticAnnotation](c: Context)(method: c.universe.MethodSymbol)(implicit tag: c.WeakTypeTag[A]): Boolean = {
    import c.universe._

    val m = method match {
      case m if m.isAccessor => m.accessed
      case m => m
    }

    m.annotations exists {
      _.tree.tpe =:= tag.tpe
    }
  }

  def hasAnnotation[A <: StaticAnnotation](c: Context)(target: c.WeakTypeTag[_], searchAncestors: Boolean)(implicit tag: c.WeakTypeTag[A]): Boolean = {
    import c.universe._

    def find(symbol: Symbol): Boolean = symbol.annotations exists {
      _.tree.tpe =:= tag.tpe
    }

    if (searchAncestors) {
      target.tpe.baseClasses.exists(find)
    } else {
      find(target.tpe.typeSymbol)
    }
  }
}