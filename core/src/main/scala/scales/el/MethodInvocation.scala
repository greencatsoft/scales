package scales.el

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.runtime.UndefinedBehaviorError
import scala.util.{ Failure, Success, Try }
import scala.util.control.NonFatal

case class MethodInvocation(
  path: Seq[Identifier], name: Identifier, arguments: Seq[Expression]) extends PathExpression {
  require(path != null, "Missing argument 'path'.")
  require(name != null, "Missing argument 'name'.")
  require(arguments != null, "Missing argument 'arguments'.")

  override def evaluate[T](context: ExpressionContext): Try[T] = {
    require(context != null, "Missing argument 'context'.")

    resolve[T](context) match {
      case Some(target) =>
        val value = target.asInstanceOf[js.Dynamic].selectDynamic(name.name)
        val function = value.asInstanceOf[UndefOr[js.Function]]

        function.toOption match {
          case Some(f) =>
            val result = Try(arguments.map(_.evaluate[js.Any](context)).map(_.get)) map {
              args =>
                try {
                  Success(f.call(target.asInstanceOf[js.Any], args: _*)).map(_.asInstanceOf[T])
                } catch {
                  case e @ (NonFatal(_) | _: UndefinedBehaviorError) => Failure{
                    new EvaluationFailureException(s"Failed to invoke a method: $this", e)
                  }
                }
            }

            result.flatten
          case None => Failure {
            new EvaluationFailureException(
              s"Unable to find a function named '$name' in object '$path'.")
          }
        }
      case None => Failure {
        new EvaluationFailureException(
          s"Unable to resolve property reference '$path' in the given context.")
      }
    }
  }

  override def toString(): String = {
    val arg = arguments.map(_.toString).mkString("(", ",", ")")
    (path :+ name).map(_.name).mkString("", ".", arg)
  }
}
