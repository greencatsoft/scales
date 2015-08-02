package scales.component.internal

import scala.scalajs.js
import scala.scalajs.js.{ undefined, UndefOr }
import scala.scalajs.js.Dynamic.global

import org.scalajs.dom.{ console, Element }

import scales.component.{ AttributeChangeAware, Component }

case class ComponentDefinition[A <: Component[_]](
  name: String,
  prototype: js.Object,
  tag: Option[String],
  properties: Seq[PropertyDefinition],
  methods: Seq[MethodDefinition],
  factory: () => A) extends Metadata {

  require(name != null, "Missing argument 'name'.")
  require(prototype != null, "Missing argument 'prototype'.")
  require(tag != null, "Missing argument 'tag'.")
  require(properties != null, "Missing argument 'properties'.")
  require(methods != null, "Missing argument 'methods'.")
  require(factory != null, "Missing argument 'factory'.")

  override def define(prototype: js.Dynamic): js.Dynamic = (properties ++ methods) match {
    case head :: tail => tail.foldLeft(head.define(defineCallbacks(prototype)))((d, p) => p.define(d))
    case _ => defineCallbacks(prototype)
  }

  def defineCallbacks(prototype: js.Dynamic): js.Dynamic = {
    val createdCallback: js.ThisFunction0[ComponentProxy[A], Unit] =
      (proxy: ComponentProxy[A]) => {
        val element = proxy.asInstanceOf[Element]
        val component = factory()

        proxy.component = component

        component.asInstanceOf[Component[Element]].onCreate(element)
      }

    val attachedCallback: js.ThisFunction0[ComponentProxy[A], Unit] =
      (proxy: ComponentProxy[A]) => proxy.component foreach { c =>
        val element = proxy.asInstanceOf[Element]
        val component = c.asInstanceOf[Component[Element]]

        component.onAttach(element)
      }

    val detachedCallback: js.ThisFunction0[ComponentProxy[A], Unit] =
      (proxy: ComponentProxy[A]) => proxy.component foreach { c =>
        val element = proxy.asInstanceOf[Element]
        val component = c.asInstanceOf[Component[Element]]

        component.onDetach(element)
      }

    val attributeChangedCallback: js.ThisFunction3[ComponentProxy[A], String, Any, Any, Unit] =
      (proxy: ComponentProxy[A], name: String, oldValue: Any, newValue: Any) =>
        proxy.component foreach {
          case l: AttributeChangeAware[_] =>
            val element = proxy.asInstanceOf[Element]
            val component = l.asInstanceOf[AttributeChangeAware[Element]]

            component.onAttributeChange(name, oldValue, newValue, element)
          case _ =>
        }

    prototype.createdCallback = createdCallback
    prototype.attachedCallback = attachedCallback
    prototype.detachedCallback = detachedCallback
    prototype.attributeChangedCallback = attributeChangedCallback

    prototype
  }
}

object ComponentDefinition {

  private val NamePattern = "^([a-zA-Z0-9]+)-([a-zA-Z0-9]+)(-([a-zA-Z0-9]+))*$".r

  private val ReservedNames = Set("annotation-xml", "color-profile", "font-face", "font-face-src",
    "font-face-uri", "font-face-format", "font-face-name", "missing-glyph")

  def isValidName(name: String): Boolean = name match {
    case NamePattern(_*) => true
    case _ => false
  }

  def isReservedName(name: String): Boolean = ReservedNames.contains(name)

  def prototype(name: Option[String]): js.Object = {
    val value: UndefOr[js.Dynamic] = name match {
      case Some(n) => global.selectDynamic(n)
      case None => undefined
    }

    val resolved = value getOrElse {
      console.warn(
        s"Failed to resolve prototype object for '$name'. Falling back to default ('HTMLElement').")

      global.selectDynamic("HTMLElement")
    }

    resolved.prototype.asInstanceOf[js.Object]
  }
}

