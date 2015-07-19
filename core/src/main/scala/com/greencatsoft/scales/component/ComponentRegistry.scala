package com.greencatsoft.scales.component

import scala.language.experimental.macros

import scala.scalajs.js

import org.scalajs.dom.{ Document, document }

import com.greencatsoft.scales.dom.ElementRegistrationOptions
import com.greencatsoft.scales.dom.ImplicitConversions.asElementRegistry
import com.greencatsoft.scales.component.internal.ComponentDefinition

object ComponentRegistry {

  @throws[MissingMetadataException](
    "Thrown when the specified type does not have sufficient information to define a component.")
  @throws[InvalidMetadataException](
    "Thrown when the specified type does not have sufficient information to define a component.")
  @throws[DuplicateDefinitionException](
    "Thrown when the specified type does not have sufficient information to define a component.")
  def register[A <: Component[_]](implicit doc: Document = document): Option[String] = macro internal.ComponentRegistryImpl.register[A]

  // def register[A <: Component[_]](implicit doc: Document = document) =  {
  //    val definition = ComponentDefinition[A]
  //    val configuration = definition.define()
  //
  //    val prototype = js.Object.create(definition.prototype, configuration)
  //    val options = ElementRegistrationOptions(Some(prototype), definition.tag)
  //
  //    doc.registerElement(definition.name, options)
  //  }
}