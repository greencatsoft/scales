package scales

package object el {

  case class Literal(value: Any) extends Expression

  case class Identifier(name: String) extends Expression
}