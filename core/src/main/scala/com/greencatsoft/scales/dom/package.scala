package com.greencatsoft.scales

import scala.language.implicitConversions

import scala.scalajs.js

import org.scalajs.dom.{ Document, Element }

package object dom {

  object ImplicitConversions extends LowPriorityImplicits

  trait LowPriorityImplicits {

    implicit def asElementFactory(document: Document): ElementFactory =
      document.asInstanceOf[ElementFactory]

    implicit def asElementRegistry(document: Document): ElementRegistry =
      document.asInstanceOf[ElementRegistry]

    implicit def asObservable(obj: js.Object): Observer = obj.asInstanceOf[Observer]

    implicit def asShadowHost(element: Element): ShadowHost =
      element.asInstanceOf[ShadowHost]
  }
}