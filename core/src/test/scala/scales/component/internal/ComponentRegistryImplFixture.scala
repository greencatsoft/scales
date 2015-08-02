package scales.component.internal

import scala.language.experimental.macros
import scala.annotation.StaticAnnotation

import scales.component.Component

object ComponentRegistryImplFixture {

  def getPrototype[A <: Component[_]](): Option[String] = macro ComponentRegistryImpl.getPrototypeExpr[A]

  def getProperties[A <: Component[_]](): Seq[PropertyDefinition] = macro ComponentRegistryImpl.getProperties[A]

  def getMethods[A <: Component[_]](): Seq[MethodDefinition] = macro ComponentRegistryImpl.getMethods[A]
}