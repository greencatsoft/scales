package scales.el

import com.greencatsoft.greenlight.TestSuite

object ExpressionEvaluatorTest extends TestSuite {

  import ExpressionEvaluator.evaluator

  "ExpressionEvaluator.evaluator" should "return an evaluator function when given a valid expression" in {
    val context = TestExpressionContext("property" -> "abc")

    val result = evaluator[String]("%{property}")

    result.isRight should be (true)
    result.isLeft should be (false)

    result.right.map(_.apply(context)) match {
      case Right(result) =>
        result.isSuccess should be (true)
        result.get should be ("abc")
      case _ =>
    }
  }

  It should "return the given argument when it's not a valid expression" in {
    val context = TestExpressionContext("property" -> "abc")

    val result = evaluator[String]("property")

    result.isRight should be (false)
    result.isLeft should be (true)

    result.right.map(_.apply(context)) match {
      case Left(result) =>
        result should be ("property")
      case _ =>
    }
  }

  It should "return an instance of EvaluationFailureException when it fails to evaluate the given expression" in {
    val context = TestExpressionContext("property" -> "abc")

    val result = evaluator[String]("%{property.dummy}")

    result.isRight should be (true)
    result.isLeft should be (false)

    result.right.map(_.apply(context)) match {
      case Right(result) =>
        result.isFailure should be (true)

        val throwable = result.failed.toOption

        throwable.exists(_.isInstanceOf[EvaluationFailureException]) should be (true)
      case _ =>
    }
  }
}