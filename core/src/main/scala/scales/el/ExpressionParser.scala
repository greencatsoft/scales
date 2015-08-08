package scales.el

import scala.language.postfixOps
import scala.util.parsing.combinator.JavaTokenParsers

object ExpressionParser extends JavaTokenParsers {

  def identifier: Parser[Identifier] = "[a-zA-Z]+[a-zA-Z0-9_]*".r ^^ Identifier

  def path: Parser[Seq[Identifier]] = rep1sep(identifier, ".")

  def nonEmptyPath: Parser[Seq[Identifier]] = rep1sep(identifier, ".")

  def quotedLiteral: Parser[String] = "'([^']*)'".r ^^ (_.drop(1).dropRight(1))

  def booleanLiteral: Parser[Boolean] = ("true" | "false") ^^ (_.toBoolean)

  def longLiteral: Parser[Long] = wholeNumber ^^ (_.toLong)

  def doubleLiteral: Parser[Double] = (floatingPointNumber | decimalNumber) ^^ (_.toDouble)

  def literal: Parser[Literal] = ("null" | quotedLiteral | booleanLiteral | doubleLiteral | longLiteral) ^^ Literal

  def argument: Parser[Expression] = (literal | invocation | reference)

  def reference: Parser[PropertyReference] = path ^^ PropertyReference

  def invocation: Parser[MethodInvocation] = nonEmptyPath ~ ("(" ~> repsep(argument, ",") <~ ")") ^^ {
    case p ~ args if p.size == 1 => MethodInvocation(Nil, p.head, args)
    case p ~ args => MethodInvocation(p.dropRight(1), p.last, args)
  }

  def expression: Parser[Expression] = "%{" ~> (literal | invocation | reference) <~ "}"
}
