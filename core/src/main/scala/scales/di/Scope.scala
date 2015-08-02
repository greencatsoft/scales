package scales.di

import org.scalajs.dom.Element

trait Scope {

  def parent: Option[Scope]

  def resolve[A](name: Option[String] = None): Option[A] = {
    doResolve[A](name) match {
      case dep @ Some(_) => dep
      case None => parent.map(_.resolve[A](name)).flatten
    }
  }

  protected def doResolve[A](name: Option[String]): Option[A]
}

object Scope {

  def apply(element: Element): Scope = ???
}