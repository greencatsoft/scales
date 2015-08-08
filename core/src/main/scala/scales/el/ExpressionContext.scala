package scales.el

trait ExpressionContext {

  def parent: Option[ExpressionContext]

  def get[T](name: String): Option[T]

  def apply[T](name: String): Option[T] = get(name) match {
    case value @ Some(_) => value
    case None => parent.flatMap(_.apply(name))
  }
}