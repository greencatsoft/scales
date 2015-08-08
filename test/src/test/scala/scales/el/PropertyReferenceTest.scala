package scales.el

import scala.language.implicitConversions
import scala.scalajs.js.annotation.JSExport
import scala.util.Success

import com.greencatsoft.greenlight.TestSuite

object PropertyReferenceTest extends TestSuite {

  val context = TestExpressionContext("object" -> TestObject)

  "PropertyReference.evaluate(context)" should "return the value of the property matching the reference path" in {
    PropertyReference("object.string").evaluate(context) should be (Success("abcd"))
    PropertyReference("object.number").evaluate(context) should be (Success(12345))
  }

  It should "be able to resolve reference expression of arbitrary depth" in {
    PropertyReference("object.nested.boolean").evaluate(context) should be (Success(true))
    PropertyReference("object.nested.parent.number").evaluate(context) should be (Success(12345))
    PropertyReference("object.nested.parent.nested.boolean").evaluate(context) should be (Success(true))
  }

  It should "return EvaluationFailureException when there's no object matching the specified identifier" in {
    val result = PropertyReference("dummy").evaluate(context)

    result.isFailure should be (true)
    result.failed foreach {
      _.getClass should be (classOf[EvaluationFailureException])
    }

    val nested = PropertyReference("object.nested.dummy").evaluate(context)

    nested.isFailure should be (true)
    result.failed foreach {
      _.getClass should be (classOf[EvaluationFailureException])
    }
  }

  implicit def path(expression: String): Seq[Identifier] = expression.split('.').map(Identifier)

  object TestObject {

    @JSExport
    val string = "abcd"

    @JSExport
    val number = 12345

    @JSExport
    val nested = AnotherObject
  }

  object AnotherObject {

    @JSExport
    val boolean = true

    @JSExport
    val parent = TestObject
  }
}