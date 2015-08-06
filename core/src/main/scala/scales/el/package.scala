package scales

package object el {

  case class Literal(value: Any)

  case class Identifier(name: String)
}