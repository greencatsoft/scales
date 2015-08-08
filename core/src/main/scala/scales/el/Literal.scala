package scales.el

import scala.util.{ Success, Try }

case class Literal(value: Any) extends Expression {

  override def evaluate[T](context: ExpressionContext): Try[T] =
    Success(value.asInstanceOf[T])

  override def toString(): String = {
    val label = Option(value) map {
      case v: String => s"'$v'"
      case v => v.toString
    }

    label getOrElse "null"
  }
}