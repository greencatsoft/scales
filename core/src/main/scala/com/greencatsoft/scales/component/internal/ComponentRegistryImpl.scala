package com.greencatsoft.scales.component.internal

import scala.reflect.macros.blackbox.Context
import org.scalajs.dom.{ Document, document }
import scala.annotation.StaticAnnotation

import com.greencatsoft.scales.component.name
import com.greencatsoft.scales.macros.AnnotationUtils

object ComponentRegistryImpl {

  def register[A](c: Context)(doc: c.Expr[Document])(implicit tag: c.WeakTypeTag[A]): c.Expr[Option[String]] = {
    import c.universe._

    AnnotationUtils.getValue[A, name](c)
    ???
  }
}