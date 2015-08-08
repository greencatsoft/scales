package scales.el

trait PathExpression extends Expression {

  def path: Seq[Identifier]
}