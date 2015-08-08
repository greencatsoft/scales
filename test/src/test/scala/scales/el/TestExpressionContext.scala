package scales.el

case class TestExpressionContext(
  values: Map[String, Any],
  parent: Option[ExpressionContext] = None) extends ExpressionContext {
  require(values != null, "Missing argument 'values'.")

  def get[T](name: String): Option[T] = values.get(name).map(_.asInstanceOf[T])
}

object TestExpressionContext {

  def apply(values: (String, Any)*): ExpressionContext = TestExpressionContext(values.toMap)
}