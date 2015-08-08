package scales.el

import scala.util.{ Success, Try }
import scala.util.Failure

case class Identifier(name: String) extends Expression {
  require(name != null, "Missing argument 'name'.")

  override def evaluate[T](context: ExpressionContext): Try[T] = {
    require(context != null, "Missing argument 'context'.")

    context[T](name) match {
      case Some(value) => Success(value)
      case None => Failure {
        new EvaluationFailureException(
          s"Unable to resolve '$name' in the given context.")
      }
    }
  }

  override def toString(): String = name
}