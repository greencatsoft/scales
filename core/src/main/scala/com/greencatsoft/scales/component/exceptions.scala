package com.greencatsoft.scales.component

class ComponentException(message: String) extends Exception(message)

class RegistrationFailedException(message: String) extends ComponentException(message)

class MissingMetadataException(message: String) extends RegistrationFailedException(message)

class InvalidMetadataException(message: String) extends RegistrationFailedException(message)

class DuplicateDefinitionException(message: String) extends RegistrationFailedException(message)
