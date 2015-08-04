package scales.component

import org.scalajs.dom.{ Document, Element, Node }

import scales.dom.Template

import scalatags.JsDom.TypedTag

trait ContentProvider[A <: Element] extends LifecycleAware[A] {
  this: Component[A] =>

  def build(document: Document): Node

  override def onCreate(element: A) {
    super.onCreate(element)

    val document = element.ownerDocument
    val child = build(document)

    contentRoot.appendChild(child)
  }
}

trait TemplateContentProvider[A <: Element] extends ContentProvider[A] {
  this: Component[A] =>

  def templateSelector: String

  override def build(document: Document): Node = {
    val template = Option(document.querySelector(templateSelector)) getOrElse {
      s"Failed to find the specified template node '$templateSelector'."
    }

    val content = template.asInstanceOf[Template].content

    document.importNode(content, true)
  }
}

trait ScalaTagsContentProvider[A <: Element] extends ContentProvider[A] {
  this: Component[A] =>

  def template: TypedTag[Element]

  override def build(document: Document): Node = document.importNode(template.render, true)
}