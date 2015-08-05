package scales.component.internal

import scala.reflect.macros.blackbox.Context

import scala.scalajs.js
import scala.scalajs.js.Dynamic.newInstance
import scala.scalajs.js.annotation.{ JSExport, JSExportAll, JSName }

import org.scalajs.dom.Document

import scales.component.{ Component, enumerable, name, prototype, tag }
import scales.dom.{ ElementRegistrationOptions, LowPriorityImplicits }
import scales.macros.AnnotationUtils

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

    val constructor = q"""
      import scales.component._
      import scales.component.internal._
      import scales.di._

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
      val factory = () => ServiceFactory.newInstance[..$tpe](GlobalScope)

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

    def isValid(symbol: Symbol, annotatedOnly: Boolean) =
      symbol.isPublic &&
        symbol.isMethod &&
        (!annotatedOnly || AnnotationUtils.hasMemberAnnotation[JSExport](c)(symbol.asMethod))

    val declared = if (hasExportAll) tag.tpe.decls.filter(isValid(_, false)) else Nil
    val annotated = tag.tpe.members.filter(isValid(_, true))

    val methods = (declared ++ annotated).map(_.asMethod).toList.distinct

    def isCustomGetter(method: MethodSymbol): Boolean =
      method.paramLists.isEmpty && !(method.returnType =:= typeOf[Unit])

    def isCustomSetter(method: MethodSymbol): Boolean =
      method.name.decodedName.toString.endsWith("_=") &&
        !method.paramLists.isEmpty &&
        method.paramLists.head.size == 1 &&
        method.returnType =:= typeOf[Unit]

    def name(method: MethodSymbol): String = method.name.decodedName.toString
    val setters = methods.filter(isCustomSetter) map { m =>
      val getter = {
        val n = name(m)
        n.substring(0, n.length - 2)
      }

      (getter, m)
    }

    val setterMap = setters.toMap

    val properties = methods collect {
      case m if m.isGetter => (m, !m.setter.isMethod)
      case m if isCustomGetter(m) => (m, !setterMap.contains(name(m)))
    } map {
      case (m, readOnly) =>
        val n = AnnotationUtils.getMemberAnnotation[JSName](c)(m) getOrElse {
          name(m)
        }

        val enumerable = AnnotationUtils.hasMemberAnnotation[enumerable](c)(m)

        (n, readOnly, enumerable)
    }

    c.Expr[Seq[PropertyDefinition]] {
      q"""
        import scales.component.internal.PropertyDefinition

        Seq[(String, Boolean, Boolean)](..$properties) map {
          case (name, readOnly, enumerable) => PropertyDefinition(name, readOnly, enumerable)
        }
      """
    }
  }

  def getMethods[A <: Component[_]](c: Context)()(implicit tag: c.WeakTypeTag[A]): c.Expr[Seq[MethodDefinition]] = {
    import c.universe._

    val hasExportAll = AnnotationUtils.hasAnnotation[JSExportAll](c)(tag, false)

    def isValid(method: MethodSymbol, annotatedOnly: Boolean) =
      method.isPublic &&
        !method.isConstructor &&
        !method.isGetter &&
        !method.isSetter &&
        !method.name.decodedName.toString.endsWith("_=") &&
        !method.paramLists.isEmpty &&
        (!annotatedOnly || AnnotationUtils.hasMemberAnnotation[JSExport](c)(method))

    def find(annotatedOnly: Boolean): PartialFunction[Symbol, Tree] = {
      case m: MethodSymbol if isValid(m, annotatedOnly) =>
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

    val declared = if (hasExportAll) tag.tpe.decls collect find(false) else Nil
    val annotated = tag.tpe.members collect find(true)

    val methods = (declared ++ annotated).toList.distinct

    c.Expr[Seq[MethodDefinition]] {
      Apply(Select(Ident(TermName("Seq")), TermName("apply")), methods)
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