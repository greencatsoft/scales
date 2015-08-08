package scales.el

import scala.language.implicitConversions
import scala.scalajs.js.annotation.JSExport
import scala.util.{ Failure, Success }

import com.greencatsoft.greenlight.TestSuite

object MethodInvocationTest extends TestSuite {

  val context = TestExpressionContext("object" -> TestObject)

  "MethodInvocation.evaluate(context)" should "invoke the method with the given arguments and return its result" in {
    MethodInvocation("object", "string", Nil).evaluate(context) should be (Success("abcd"))
    MethodInvocation("object", "add", Seq(Literal(2), Literal(3))).evaluate(context) should be (Success(5))
  }

  It should "be able to resolve reference expression of arbitrary depth" in {
    MethodInvocation("object.nested", "echo", Seq(Literal("ping!"))).evaluate(context) should be (Success("ping!"))
    MethodInvocation("object.nested.parent", "string", Nil).evaluate(context) should be (Success("abcd"))
  }

  It should "be able to handle complex argument types" in {
    val property = PropertyReference("object.label")

    MethodInvocation("object.nested", "echo", Seq(property)).evaluate(context) should be (Success("efgh"))

    val invocation = MethodInvocation("object.nested", "echo", Seq(Literal("ping!")))

    MethodInvocation("object.nested", "echo", Seq(invocation)).evaluate(context) should be (Success("ping!"))
  }

  It should "return EvaluationFailureException when there's no method matching the specified name" in {
    val result = MethodInvocation("object", "dummy", Nil).evaluate(context)

    result.isFailure should be (true)
    result.failed foreach {
      _.getClass should be (classOf[EvaluationFailureException])
    }

    val nested = MethodInvocation("object.nested", "dummy", Nil).evaluate(context)

    nested.isFailure should be (true)
    result.failed foreach {
      _.getClass should be (classOf[EvaluationFailureException])
    }
  }

  It should "return EvaluationFailureException when the given arguments do not match the method's signature" in {
    val result = MethodInvocation("object", "add", Nil).evaluate(context)

    result.isFailure should be (true)
    result.failed foreach {
      _.getClass should be (classOf[EvaluationFailureException])
    }
  }

  It should "return EvaluationFailureException when the invoked method throws an exception" in {
    val result = MethodInvocation("object", "bad", Nil).evaluate(context)

    result.isFailure should be (true)
    result.failed foreach {
      _.getClass should be (classOf[EvaluationFailureException])
    }

    result.failed match {
      case Failure(e: EvaluationFailureException) =>
        val cause = Option(e.getCause)

        cause should not be (empty)
        cause.map(_.getMessage) should be (Some("Bad, bad boy!"))
      case _ =>
    }
  }

  implicit def ident(name: String): Identifier = Identifier(name)

  implicit def path(expression: String): Seq[Identifier] = expression.split('.').map(Identifier)

  object TestObject {

    @JSExport
    def string() = "abcd"

    @JSExport
    val label = "efgh"

    @JSExport
    def add(a: Int, b: Int) = a + b

    def bad() = throw new Exception("Bad, bad boy!")

    @JSExport
    val nested = AnotherObject
  }

  object AnotherObject {

    @JSExport
    def echo(label: String) = label

    @JSExport
    def refence(ref: Any, postfix: String) = ref.toString + postfix

    @JSExport
    val parent = TestObject
  }
}