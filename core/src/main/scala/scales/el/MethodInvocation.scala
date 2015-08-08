package scales.el

case class MethodInvocation(path: Seq[Identifier], name: Identifier, arguments: Seq[Expression]) extends Expression
