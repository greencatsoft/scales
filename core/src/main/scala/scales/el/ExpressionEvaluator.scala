package scales.el

import scala.util.Try

import ExpressionParser.{ Error, Failure, Success, expression => parser, parseAll }

object ExpressionEvaluator {

  type Evaluator[T] = ExpressionContext => Try[T]

  def evaluator[T](expression: String): Either[String, Evaluator[T]] = {
    parseAll(parser, expression) match {
      case Success(expr, _) => Right {
        (context: ExpressionContext) => expr.evaluate[T](context)
      }
      case Error(message, _) => Right {
        (_: ExpressionContext) => throw new EvaluationFailureException(message)
      }
      case Failure(_, _) => Left {
        expression
      }
    }
  }
}