package com.greencatsoft.scales.component.internal

import scala.language.experimental.macros
import scala.annotation.StaticAnnotation

object ComponentDefinitionFixture {

  import ComponentDefinition.Macros

  def getPrototype[A](): Option[String] = macro Macros.getPrototypeExpr[A]
}