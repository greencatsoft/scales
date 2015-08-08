package scales.el

import scala.scalajs.js
import scala.scalajs.js.UndefOr

trait PathExpression extends Expression {

  def path: Seq[Identifier]

  def resolve[T](context: ExpressionContext): Option[T] = {
    require(context != null, "Missing argument 'context'.")

    def find(segments: List[Identifier], value: Any): Option[T] = segments match {
      case Identifier(name) :: tail =>
        val result: UndefOr[js.Dynamic] =
          value.asInstanceOf[js.Dynamic].selectDynamic(name)

        result.toOption match {
          case Some(v) => find(tail, v)
          case None => None
        }
      case Nil => Option(value).map(_.asInstanceOf[T])
    }

    path.toList match {
      case Identifier(name) :: tail => context[T](name).map(find(tail, _)).flatten
      case Nil => None
    }
  }
}