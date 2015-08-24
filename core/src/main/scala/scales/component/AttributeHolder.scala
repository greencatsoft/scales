package scales.component

import scala.util.{ Left, Right }

import org.scalajs.dom.Element

import scales.binding.{ ConversionFailureException, Converter, ConverterNotFoundException, ConverterSupport }
import scales.el.{ EvaluationFailureException, ExpressionEvaluator }

trait AttributeHolder[A <: Element] extends ConverterSupport
  with LifecycleAware[A] with AttributeChangeAware[A] {
  this: Component[A] =>

  import AttributeHolder._

  @throws[ConversionFailureException](
    "Thrown when it fails to convert the attribute's value to the specified type.")
  @throws[ConverterNotFoundException](
    "Thrown when it fails to find a suitable converter in the scope to convert the attribute's value.")
  @throws[EvaluationFailureException](
    "Thrown when it fails to evalute the attribute's expression.")
  def getAttribute[T: Converter](name: String): Option[T] = {
    require(name != null, "Missing argument 'name'.")

    val attribute = Option(element.getAttribute(name)).map(_.trim).filterNot(_.isEmpty)

    val result = attribute.map(ExpressionEvaluator.evaluator[T]) map {
      case Right(evaluator) => evaluator(context)
      case Left(value) => implicitly[Converter[T]].apply(value)
    }

    result.map(_.get)
  }

  def evaluateAttributes() {
    val attributes =
      for (i <- 0 until element.attributes.length)
        yield element.attributes.item(i).name

    attributes.filter(isAttributeOverride) foreach {
      evaluateAttribute(_)
    }
  }

  @throws[EvaluationFailureException](
    "Thrown when it fails to evalute the attribute's expression.")
  def evaluateAttribute(name: String): Unit = {
    getAttribute[String](name) foreach {
      element.setAttribute(getOverridenAttribute(name), _)
    }
  }

  override def onAttach(element: A) {
    super.onAttach(element)
    evaluateAttributes()
  }

  override def onAttributeChange(name: String, oldValue: Any, newValue: Any, element: A) {
    super.onAttributeChange(name, oldValue, newValue, element)

    if (isAttributeOverride(name)) evaluateAttribute(name)
  }
}

object AttributeHolder {

  val AttributeOverridePrefix = "sc-"

  def isAttributeOverride(name: String): Boolean = {
    require(name != null, "Missing argument 'name'.")

    name startsWith AttributeOverridePrefix
  }

  def getOverridenAttribute(name: String): String = {
    require (isAttributeOverride(name), s"'$name' does not override another attribute.")

    name.drop(AttributeOverridePrefix.length)
  }
}