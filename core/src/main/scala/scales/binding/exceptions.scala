package scales.binding

class ConversionException(message: String) extends Exception(message)

class ConversionFailureException(message: String, val value: String) extends ConversionException(message)

class ConverterNotFoundException(message: String, val tpe: Class[_]) extends Exception(message)
