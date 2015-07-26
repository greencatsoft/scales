package com.greencatsoft.scales.component.internal

import scala.reflect.macros.blackbox.Context

import scala.scalajs.js
import scala.scalajs.js.Dynamic.newInstance
import scala.scalajs.js.annotation.{ JSExport, JSExportAll, JSName }

import org.scalajs.dom.Document

import com.greencatsoft.scales.component.{ Component, enumerable, name, prototype, tag }
import com.greencatsoft.scales.dom.{ ElementRegistrationOptions, LowPriorityImplicits }
import com.greencatsoft.scales.macros.AnnotationUtils

object ComponentRegistryImpl extends LowPriorityImplicits {

  def register[A <: Component[_]](c: Context)(doc: c.Expr[Document])(implicit t: c.WeakTypeTag[A]): c.Expr[Function0[A]] = {
    import c.universe._

    val name = AnnotationUtils.getValueExpr[name](c)(t)
    val tag = AnnotationUtils.getValueExpr[tag](c)(t)

    val prototype = {
      AnnotationUtils.getValue[prototype](c)(t) match {
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

    val members = t.tpe.members collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor => m
    }

    val ctor = members getOrElse {
      c.abort(c.enclosingPosition, s"The specified type '${t.tpe}' does not have a suitable constructor.")
    }

    val properties = getProperties[A](c)()(t)
    val methods = getMethods[A](c)()(t)

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

      val properties = {..$properties}
      val methods = {..$methods}

      val definition = ComponentDefinition[..$tpe](name, prototype, {..$tag}, properties, methods, factory)

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

  def getProperties[A <: Component[_]](c: Context)()(implicit tag: c.WeakTypeTag[A]): c.Expr[Seq[PropertyDefinition]] = {
    import c.universe._

    val hasExportAll = AnnotationUtils.hasAnnotation[JSExportAll](c)(tag, false)

    def isValid(method: MethodSymbol) =
      method.isPublic &&
        method.isGetter &&
        (hasExportAll || AnnotationUtils.hasMemberAnnotation[JSExport](c)(method))

    val properties = tag.tpe.members collect {
      case m: MethodSymbol if isValid(m) =>
        val name = AnnotationUtils.getMemberAnnotation[JSName](c)(m) getOrElse {
          m.name.decodedName.toString
        }

        val readOnly = !m.setter.isMethod
        val enumerable = AnnotationUtils.hasMemberAnnotation[enumerable](c)(m)

        (name, readOnly, enumerable)
    }

    c.Expr[Seq[PropertyDefinition]] {
      q"""
        import com.greencatsoft.scales.component.internal.PropertyDefinition

        Seq[(String, Boolean, Boolean)](..$properties) map {
          case (name, readOnly, enumerable) => PropertyDefinition(name, readOnly, enumerable)
        }
      """
    }
  }

  def getMethods[A <: Component[_]](c: Context)()(implicit tag: c.WeakTypeTag[A]): c.Expr[Seq[MethodDefinition]] = {
    import c.universe._

    val hasExportAll = AnnotationUtils.hasAnnotation[JSExportAll](c)(tag, false)

    def isValid(method: MethodSymbol) =
      method.isPublic &&
        !method.isConstructor &&
        !method.isGetter &&
        !method.isSetter &&
        (hasExportAll || AnnotationUtils.hasMemberAnnotation[JSExport](c)(method))

    val methods = tag.tpe.members collect {
      case m: MethodSymbol if isValid(m) =>
        val name = AnnotationUtils.getMemberAnnotation[JSName](c)(m) getOrElse {
          m.name.decodedName.toString
        }

        val arguments = m.paramLists.headOption.toSeq.flatten map {
          name => Literal(Constant(name.asTerm.name.toString))
        }

        val arg1 = Literal(Constant(name))
        val arg2 = Apply(Select(Ident(TermName("Seq")), TermName("apply")), arguments.toList)

        Apply(Select(Ident(TermName("MethodDefinition")), TermName("apply")), List(arg1, arg2))
    }

    c.Expr[Seq[MethodDefinition]] {
      Apply(Select(Ident(TermName("Seq")), TermName("apply")), methods.toList)
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