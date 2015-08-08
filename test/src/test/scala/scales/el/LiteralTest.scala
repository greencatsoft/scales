package scales.el

import scala.util.Success

import com.greencatsoft.greenlight.TestSuite

object LiteralTest extends TestSuite {

  "Literal.evaluate(context)" should "return the literal value of the instance" in {
    val context = TestExpressionContext()

    Literal("Nothing fancy!").evaluate(context) should be (Success("Nothing fancy!"))
    Literal(true).evaluate(context) should be (Success(true))
    Literal(123).evaluate(context) should be (Success(123))
    Literal(null).evaluate(context) should be (Success(null))
  }
}