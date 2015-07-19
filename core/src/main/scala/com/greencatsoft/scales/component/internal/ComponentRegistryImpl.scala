package com.greencatsoft.scales.component.internal

import scala.reflect.macros.blackbox.Context

import org.scalajs.dom.Document

import com.greencatsoft.scales.component.{ Component, name, prototype }
import com.greencatsoft.scales.macros.AnnotationUtils

private[component] object ComponentRegistryImpl {

  def register[A <: Component[_]](c: Context)(doc: c.Expr[Document])(implicit tag: c.WeakTypeTag[A]): c.Expr[Option[String]] = {
    import c.universe._

    val name = AnnotationUtils.getValueExpr[A, name](c)()(tag, typeTag[name])
    val prototype = AnnotationUtils.getValueExpr[A, prototype](c)()(tag, typeTag[prototype])

    val constructor = q"""
      import com.greencatsoft.scales.component._
      import com.greencatsoft.scales.component.internal._

      val name = {..$name} getOrElse {
        throw new MissingMetadataException(
          "The specified component does not have a '@name' annotation.")
      }

      if (!ComponentDefinition.isValidName(name)) {
        throw new InvalidMetadataException(
          s"'$$name' is not a valid name for a custom element.")
      }

      if (ComponentDefinition.isReservedName(name)) {
        throw new InvalidMetadataException(s"'$$name' is a reserved name.")
      }

    //    val definition = ComponentDefinition[A](name, prototype, tag, properties)
    //    val configuration = definition.define()
    //
    //    val prototype = js.Object.create(definition.prototype, configuration)
    //    val options = ElementRegistrationOptions(Some(prototype), definition.tag)
    //
    //    doc.registerElement(definition.name, options)

      None
    """

    c.Expr[Option[String]](constructor)
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