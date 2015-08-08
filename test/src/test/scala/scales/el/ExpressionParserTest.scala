package scales.el

import com.greencatsoft.greenlight.TestSuite

object ExpressionParserTest extends TestSuite {

  import ExpressionParser._

  "ExpressionParser.quotedLiteral" should "parse a single quoted string literal" in {
    find(quotedLiteral)("'abc'") should be ("abc")
    find(quotedLiteral)("' a b c '") should be (" a b c ")
    find(quotedLiteral)("''") should be ("")
  }

  It should "report a failure when given an invalid quoted string literal" in {
    isSuccessful(quotedLiteral)("`abc") should be (false)
    isSuccessful(quotedLiteral)("abc") should be (false)
    isSuccessful(quotedLiteral)("'ab'c'") should be (false)
  }

  "ExpressionParser.booleanLiteral" should "parse a boolean type literal value" in {
    find(booleanLiteral)("true") should be (true)
    find(booleanLiteral)("false") should be (false)
  }

  It should "report a failure when given an invalid quoted boolean literal" in {
    isSuccessful(booleanLiteral)("'true'") should be (false)
    isSuccessful(booleanLiteral)("False") should be (false)
    isSuccessful(booleanLiteral)("true1") should be (false)
  }

  "ExpressionParser.longLiteral" should "parse a whole number and return the result as a Long value" in {
    find(longLiteral)("123") should be (123L)
    find(longLiteral)("-123456789") should be (-123456789L)
  }

  It should "report a failure when given an invalid whole number value" in {
    isSuccessful(longLiteral)("'123'") should be (false)
    isSuccessful(longLiteral)("12.345") should be (false)
    isSuccessful(longLiteral)("123L") should be (false)
    isSuccessful(longLiteral)("+123") should be (false)
  }

  "ExpressionParser.doubleLiteral" should "parse a decimal number and return the result as a Double value" in {
    find(doubleLiteral)("123") should be (123d)
    find(doubleLiteral)("-123.456") should be (-123.456d)
    find(doubleLiteral)(".1234") should be (.1234d)
    find(doubleLiteral)("01234") should be (1234d)
  }

  It should "report a failure when given an invalid decimal number value" in {
    isSuccessful(doubleLiteral)("'123d'") should be (false)
    isSuccessful(doubleLiteral)("123.456.789") should be (false)
    isSuccessful(doubleLiteral)("+1234.5") should be (false)
  }

  It should "parse a floating point number and return the result as a Double value" in {
    find(doubleLiteral)("123f") should be (123f)
    find(doubleLiteral)(".1234d") should be (.1234d)
    find(doubleLiteral)("01234D") should be (1234D)
    find(doubleLiteral)("-5e6f") should be (-5000000f)
    find(doubleLiteral)("2.4E4F") should be (24000f)
  }

  It should "report a failure when given an invalid floating point value" in {
    isSuccessful(doubleLiteral)("'123a'") should be (false)
    isSuccessful(doubleLiteral)("e412.789") should be (false)
    isSuccessful(doubleLiteral)("-12.34.5") should be (false)
    isSuccessful(doubleLiteral)("+12.34f") should be (false)
  }

  "ExpressionParser.identifier" should "parse a valid Java identifier" in {
    find(identifier)("property") should be (Identifier("property"))
    find(identifier)("property_") should be (Identifier("property_"))
    find(identifier)("property123") should be (Identifier("property123"))
  }

  It should "report a failure when given an invalid Java identifier" in {
    isSuccessful(identifier)("p roperty") should be (false)
    isSuccessful(identifier)("p#roperty") should be (false)
    isSuccessful(identifier)("123property") should be (false)
  }

  "ExpressionParser.path" should "parse a reference path of any depth" in {
    find(path)("a") should be (Seq(Identifier("a")))
    find(path)("a.b") should be (Seq(Identifier("a"), Identifier("b")))
    find(path)("a.b.c") should be (Seq(Identifier("a"), Identifier("b"), Identifier("c")))
  }

  It should "report a failure when given an invalid reference path" in {
    isSuccessful(path)("a b.c") should be (false)
    isSuccessful(path)("a#.b.c") should be (false)
    isSuccessful(path)("a.1b.c") should be (false)
  }

  "ExpressionParser.reference" should "parse a property reference of any depth" in {
    find(reference)("a_b_c") should be (PropertyReference(Seq(Identifier("a_b_c"))))
    find(reference)("aa.bb") should be (PropertyReference(Seq(Identifier("aa"), Identifier("bb"))))
    find(reference)("a1.b1.c1") should be (PropertyReference(Seq(Identifier("a1"), Identifier("b1"), Identifier("c1"))))
  }

  It should "report a failure when given an invalid property reference expression" in {
    isSuccessful(reference)("a b.c") should be (false)
    isSuccessful(reference)("a#.b.c") should be (false)
    isSuccessful(reference)("a.1b.c") should be (false)
  }

  "ExpressionParser.invocation" should "parse a method invocation of a referenced object of any depth" in {
    find(invocation)("abc()") should be (MethodInvocation(Nil, Identifier("abc"), Nil))
    find(invocation)("abc(1, true, 'abc')") should be (MethodInvocation(Nil, Identifier("abc"), Seq(Literal(1), Literal(true), Literal("abc"))))
    find(invocation)("a.b.c()") should be (MethodInvocation(Seq(Identifier("a"), Identifier("b")), Identifier("c"), Nil))
    find(invocation)("a.b(-2.4)") should be (MethodInvocation(Seq(Identifier("a")), Identifier("b"), Seq(Literal(-2.4d))))
  }

  It should "accept property references as arguments" in {
    find(invocation)("abc(d)") should be (MethodInvocation(Nil, Identifier("abc"), Seq(PropertyReference(Seq(Identifier("d"))))))
    find(invocation)("a.bc(d)") should be (MethodInvocation(Seq(Identifier("a")), Identifier("bc"), Seq(PropertyReference(Seq(Identifier("d"))))))
    find(invocation)("abc(de.f)") should be (MethodInvocation(Nil, Identifier("abc"), Seq(PropertyReference(Seq(Identifier("de"), Identifier("f"))))))
    find(invocation)("ab.c(d, true, ef.g, 'h')") should be (MethodInvocation(Seq(Identifier("ab")), Identifier("c"),
      Seq(PropertyReference(Seq(Identifier("d"))), Literal(true), PropertyReference(Seq(Identifier("ef"), Identifier("g"))), Literal("h"))))
  }

  It should "accept nested method invocations as arguments" in {
    find(invocation)("abc(d())") should be (MethodInvocation(Nil, Identifier("abc"), Seq(MethodInvocation(Nil, Identifier("d"), Nil))))
    find(invocation)("a.bc(d.e(true))") should be (MethodInvocation(Seq(Identifier("a")), Identifier("bc"),
      Seq(MethodInvocation(Seq(Identifier("d")), Identifier("e"), Seq(Literal(true))))))
    find(invocation)("abc(de.f(gh, true))") should be (MethodInvocation(Nil, Identifier("abc"), Seq(
      MethodInvocation(Seq(Identifier("de")), Identifier("f"), Seq(PropertyReference(Seq(Identifier("gh"))), Literal(true))))))
    find(invocation)("ab.c(d, true, ef.g(), 'h')") should be (MethodInvocation(Seq(Identifier("ab")), Identifier("c"),
      Seq(PropertyReference(Seq(Identifier("d"))), Literal(true), MethodInvocation(Seq(Identifier("ef")), Identifier("g"), Nil), Literal("h"))))
  }

  It should "report a failure when given an invalid method invocation expression" in {
    isSuccessful(invocation)("a(") should be (false)
    isSuccessful(invocation)("(true, 1, 2)") should be (false)
    isSuccessful(invocation)("a.b.c(1abc)") should be (false)
  }

  "ExpressionParser.expression" should "parse a method invocation of a referenced object of any depth" in {
    find(expression)("%{abc()}") should be (MethodInvocation(Nil, Identifier("abc"), Nil))
    find(expression)("%{ abc(1, true, '{abc}') }") should be (MethodInvocation(Nil, Identifier("abc"), Seq(Literal(1), Literal(true), Literal("{abc}"))))
    find(expression)("%{a.b.c()  }") should be (MethodInvocation(Seq(Identifier("a"), Identifier("b")), Identifier("c"), Nil))
    find(expression)("%{a.b(-2.4, '12%')}") should be (MethodInvocation(Seq(Identifier("a")), Identifier("b"), Seq(Literal(-2.4d), Literal("12%"))))
  }

  It should "report a failure when given an invalid method invocation expression" in {
    isSuccessful(expression)("%{a()") should be (false)
    isSuccessful(expression)("%{(true, 1, 2)}") should be (false)
    isSuccessful(expression)("%{a.b.c(a bc)}") should be (false)
    isSuccessful(expression)("{a.b.c('abc')}") should be (false)
  }

  It should "parse a property reference of any depth" in {
    find(expression)("%{a_b_c}") should be (PropertyReference(Seq(Identifier("a_b_c"))))
    find(expression)("%{ aa.bb}") should be (PropertyReference(Seq(Identifier("aa"), Identifier("bb"))))
    find(expression)("%{a1.b1.c1  }") should be (PropertyReference(Seq(Identifier("a1"), Identifier("b1"), Identifier("c1"))))
  }

  It should "report a failure when given an invalid property reference expression" in {
    isSuccessful(expression)("%{a b.c}") should be (false)
    isSuccessful(expression)("%{a#.b.c}") should be (false)
    isSuccessful(expression)("%{a.1b.c}") should be (false)
    isSuccessful(expression)("%{a.b.c") should be (false)
    isSuccessful(expression)("{a.b.c}") should be (false)
  }

  def find[A](parser: Parser[A])(expr: String) = parseAll(parser, expr) match {
    case Success(result, _) => result
    case Failure(message, _) => message
    case Error(message, _) => message
  }

  def isSuccessful[A](parser: Parser[A])(expr: String): Boolean = parseAll(parser, expr).successful
}
