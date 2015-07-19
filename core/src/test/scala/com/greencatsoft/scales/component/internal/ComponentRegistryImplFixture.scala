package com.greencatsoft.scales.component.internal

import scala.language.experimental.macros
import scala.annotation.StaticAnnotation

import com.greencatsoft.scales.component.Component

object ComponentRegistryImplFixture {

  def getPrototype[A <: Component[_]](): Option[String] = macro ComponentRegistryImpl.getPrototypeExpr[A]
}