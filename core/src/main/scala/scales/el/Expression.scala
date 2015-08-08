package scales.el

import scala.util.Try

trait Expression {

  def evaluate[T](context: ExpressionContext): Try[T]
}
