package com.greencatsoft.scales.component

import scala.language.experimental.macros

import org.scalajs.dom.Document

import com.greencatsoft.scales.component.internal.ComponentRegistryImpl

object ComponentRegistry {

  @throws[MissingMetadataException](
    "Thrown when the specified type does not have sufficient information to define a component.")
  @throws[InvalidMetadataException](
    "Thrown when the specified type does not have sufficient information to define a component.")
  @throws[DuplicateDefinitionException](
    "Thrown when the specified type does not have sufficient information to define a component.")
  def register[A <: Component[_]](implicit doc: Document): Option[String] = macro ComponentRegistryImpl.register[A]
}