package scales.component

import scala.util.{ Left, Right }

import org.scalajs.dom.Element

import scales.binding.{ ConversionFailureException, Converter, ConverterNotFoundException, ConverterSupport }
import scales.el.{ EvaluationFailureException, ExpressionEvaluator }

trait AttributeHolder[A <: Element] extends ConverterSupport {
  this: Component[A] =>

  @throws[ConversionFailureException](
    "Thrown when it fails to convert the attribute's value to the specified type.")
  @throws[ConverterNotFoundException](
    "Thrown when it fails to find a suitable converter in the scope to convert the attribute's value.")
  @throws[EvaluationFailureException](
    "Thrown when it fails to evalute the attribute's expression.")
  def getAttribute[T: Converter](name: String): Option[T] = {
    require(name != null, "Missing argument 'name'.")

    val attribute = Option(element.getAttribute(name)).map(_.trim).filterNot(_.isEmpty)

    val result = attribute.map(ExpressionEvaluator.evaluator[T]) map {
      case Right(evaluator) => evaluator(context)
      case Left(value) => implicitly[Converter[T]].apply(value)
    }

    result.map(_.get)
  }
}
