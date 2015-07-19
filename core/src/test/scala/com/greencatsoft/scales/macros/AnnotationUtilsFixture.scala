package com.greencatsoft.scales.macros

import scala.language.experimental.macros
import scala.annotation.StaticAnnotation

object AnnotationUtilsFixture {

  def getValue[A, B <: StaticAnnotation](): Option[String] = macro AnnotationUtils.getValueExpr[A, B]
}