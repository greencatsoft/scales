package scales.el

import scala.util.{ Failure, Success, Try }

case class PropertyReference(path: Seq[Identifier]) extends PathExpression {
  require(path != null, "Missing argument 'path'.")

  override def evaluate[T](context: ExpressionContext): Try[T] = {
    require(context != null, "Missing argument 'context'.")

    resolve[T](context) match {
      case Some(value) => Success(value)
      case None => Failure {
        new EvaluationFailureException(
          s"Unable to resolve property reference '$this' in the given context.")
      }
    }
  }

  override def toString(): String = path.map(_.name).mkString(".")
}