package com.greencatsoft.scales.component.internal

import scala.language.experimental.macros
import scala.annotation.StaticAnnotation

import com.greencatsoft.scales.component.Component

object ComponentDefinitionFixture {

  import ComponentDefinition.Macros

  def getPrototype[A <: Component[_]](): Option[String] = macro Macros.getPrototypeExpr[A]
}