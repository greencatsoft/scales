package scales.el

import scala.util.Success

import com.greencatsoft.greenlight.TestSuite

object IdentifierTest extends TestSuite {

  "Identifier.evaluate(context)" should "return an instance matching the identifier from the given context" in {
    val context = TestExpressionContext(
      "Kenny" -> "You've got to know when to hold 'em.", "Rogers" -> "Know when to fold 'em.")

    Identifier("Kenny").evaluate(context) should be (Success("You've got to know when to hold 'em."))
    Identifier("Rogers").evaluate(context) should be (Success("Know when to fold 'em."))
  }

  It should "return EvaluationFailureException when there's no object matching the specified identifier" in {
    val context = TestExpressionContext("Ginger" -> "You like tomato and I like tomahto.")

    val result = Identifier("Rogers").evaluate(context)

    result.isFailure should be (true)
    result.failed foreach {
      _.getClass should be (classOf[EvaluationFailureException])
    }
  }
}