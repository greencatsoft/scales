package scales.el

trait Expression

trait PathExpression extends Expression {

  def path: Seq[Identifier]
}

case class PropertyReference(path: Seq[Identifier]) extends Expression

case class MethodInvocation(path: Seq[Identifier], name: Identifier, arguments: Seq[Expression]) extends Expression
