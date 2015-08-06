package scales.el

trait Expression {

  def path: Seq[Identifier]
}

case class PropertyReference(path: Seq[Identifier]) extends Expression

case class MethodInvocation(path: Seq[Identifier], name: Identifier, arguments: Seq[Literal]) extends Expression

