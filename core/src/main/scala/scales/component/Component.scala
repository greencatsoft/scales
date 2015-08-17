package scales.component

import org.scalajs.dom.Element

import scales.el.{ ContextProvider, ExpressionContext }
import scales.query.NodeProvider

trait Component[A <: Element] extends NodeProvider[Element] with ContextProvider
  with AttributeHolder[A] with LifecycleAware[A] {

  private var _element: Option[A] = None

  private var _context: Option[ExpressionContext] = None

  @throws[IllegalStateException](
    "Thrown when invoked before the component is initialized.")
  def element: A = _element getOrElse {
    throw new IllegalStateException("The component has not been initialized yet.")
  }

  @throws[IllegalStateException](
    "Thrown when invoked before the component is attached to a DOM tree.")
  override def context: ExpressionContext = _context getOrElse {
    throw new IllegalStateException("The component has not been initialized yet.")
  }

  @throws[IllegalStateException](
    "Thrown when invoked before the component is initialized.")
  override def contentRoot: Element = element

  override def onCreate(element: A): Unit = {
    super.onCreate(element)

    this._element = Some(element)
    this._context = Some(new ComponentContext(this))
  }
}
