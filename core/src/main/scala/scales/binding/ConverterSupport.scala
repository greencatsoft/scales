package scales.binding

import scala.reflect.ClassTag
import scala.util.{ Success, Try }

trait ConverterSupport extends ConverterSupport.Fallback {

  implicit object stringConverter extends Converter[String] {

    override def apply(value: String): Try[String] = Success(value)
  }

  implicit object intConverter extends Converter[Int] {

    override def apply(value: String): Try[Int] = Try(value.toInt) recover {
      case _: NumberFormatException =>
        throw new ConversionFailureException(s"'$value' is not a valid instance of Int.", value)
    }
  }

  implicit object longConverter extends Converter[Long] {

    override def apply(value: String): Try[Long] = Try(value.toLong) recover {
      case _: NumberFormatException =>
        throw new ConversionFailureException(s"'$value' is not a valid instance of Long.", value)
    }
  }

  implicit object floatConverter extends Converter[Float] {

    override def apply(value: String): Try[Float] = Try(value.toFloat) recover {
      case _: NumberFormatException =>
        throw new ConversionFailureException(s"'$value' is not a valid instance of Float.", value)
    }
  }

  implicit object doubleConverter extends Converter[Double] {

    override def apply(value: String): Try[Double] = Try(value.toDouble) recover {
      case _: NumberFormatException =>
        throw new ConversionFailureException(s"'$value' is not a valid instance of Double.", value)
    }
  }

  implicit object booleanConverter extends Converter[Boolean] {

    override def apply(value: String): Try[Boolean] = Try(value.toBoolean) recover {
      case _: IllegalArgumentException =>
        throw new ConversionFailureException(s"'$value' is not a valid instance of Boolean.", value)
    }
  }

  implicit def optionConverter[T: Converter]: Converter[Option[T]] = new Converter[Option[T]] {

    val converter = implicitly[Converter[T]]

    override def apply(value: String): Try[Option[T]] = Option(value).map(_.trim).filterNot(_.isEmpty) match {
      case Some(v) => converter(v).map(Option.apply)
      case None => Success(None)
    }
  }
}

object ConverterSupport {

  trait Fallback {
    implicit def noopConverter[T](implicit tag: ClassTag[T]): Converter[T] =
      throw new ConverterNotFoundException(
        s"No suitable converter was found for type '$tag'.", tag.runtimeClass)
  }
}