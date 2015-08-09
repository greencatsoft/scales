package scales.component

import org.scalajs.dom.{ Element, Node }

import scales.el.{ ExpressionContext, ContextProvider }
import scales.query.NodeProvider

import internal.ComponentProxy

trait Component[A <: Element] extends NodeProvider[Element] with ContextProvider
  with LifecycleAware[A] {

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
  }

  override def onAttach(element: A): Unit = {
    super.onAttach(element)

    val unwrap = ComponentProxy.unwrap[Component[_]] _

    def find(node: Node): Option[Component[_]] = node match {
      case e: Element => unwrap(e) match {
        case component @ Some(_) => component
        case None => Option(e.parentNode).map(find).flatten
      }
      case _ => None
    }

    val parent = Option(element.parentNode).map(find).flatten.map(_.context)

    this._context = Some(new ComponentContext[A](element, parent))
  }

  override def onDetach(element: A): Unit = {
    this._context = None

    super.onDetach(element)
  }
}
