package scales.el

class ExpressionException(message: String, cause: Throwable)
  extends Exception(message, cause) {

  def this(message: String) {
    this(message, null)
  }
}

class EvaluationFailureException(message: String, cause: Throwable)
  extends ExpressionException(message, cause) {

  def this(message: String) {
    this(message, null)
  }
}

class InvalidExpressionException(message: String) extends ExpressionException(message)
