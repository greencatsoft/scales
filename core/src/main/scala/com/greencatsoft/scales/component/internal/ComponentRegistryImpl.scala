package com.greencatsoft.scales.component.internal

import scala.reflect.macros.blackbox.Context

import scala.scalajs.js
import scala.scalajs.js.Dynamic.newInstance

import org.scalajs.dom.Document

import com.greencatsoft.scales.component.{ Component, name, prototype, tag }
import com.greencatsoft.scales.dom.{ ElementRegistrationOptions, LowPriorityImplicits }
import com.greencatsoft.scales.macros.AnnotationUtils

private[component] object ComponentRegistryImpl extends LowPriorityImplicits {

  def register[A <: Component[_]](c: Context)(doc: c.Expr[Document])(implicit t: c.WeakTypeTag[A]): c.Expr[Function0[A]] = {
    import c.universe._

    val name = AnnotationUtils.getValueExpr[A, name](c)()(t, typeTag[name])
    val tag = AnnotationUtils.getValueExpr[A, tag](c)()(t, typeTag[tag])

    val prototype = {
      AnnotationUtils.getValue[A, prototype](c)()(t, typeTag[prototype]) match {
        case p @ Some(_) => p
        case None => getPrototype[A](c)()(t)
      }
    } match {
      case Some(value) =>
        c.Expr[Option[String]] {
          Apply(Select(Ident(TermName("Some")), TermName("apply")), List(Literal(Constant(value))))
        }
      case None => reify(None)
    }

    val tpe = List(t.tpe)

    val methods = t.tpe.members collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor => m
    }

    val ctor = methods getOrElse {
      c.abort(c.enclosingPosition, s"The specified type '${t.tpe}' does not have a suitable constructor.")
    }

    // Should be delegated to a proper DI container(ServiceFactory) later . 
    val factory = Apply(Select(New(Ident(t.tpe.typeSymbol)), termNames.CONSTRUCTOR), Nil)

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

      val prototype = ComponentDefinition.prototype({..$prototype})
      val factory = () => {..$factory}
      val definition = ComponentDefinition[..$tpe](name, prototype, {..$tag}, Nil, factory)

      ComponentRegistryImpl.registerDefinition(definition, ..$doc)
    """

    c.Expr[Function0[A]](constructor)
  }

  def registerDefinition[A <: Component[_]](definition: ComponentDefinition[A], doc: Document): Function0[A] = {
    val parent = js.Object.create(definition.prototype).asInstanceOf[js.Dynamic]
    val prototype = definition.define(parent).asInstanceOf[js.Object]

    val options = ElementRegistrationOptions(Some(prototype), definition.tag)

    val constructor = doc.registerElement(definition.name, options)

    () => {
      val proxy = newInstance(constructor)().asInstanceOf[ComponentProxy[A]]

      proxy.component getOrElse {
        throw new IllegalStateException("The component has not been initialized yet.")
      }
    }
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