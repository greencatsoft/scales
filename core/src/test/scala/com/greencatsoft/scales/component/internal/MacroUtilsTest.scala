package com.greencatsoft.scales.component.internal

import org.scalajs.dom.Element
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.HTMLInputElement

import com.greencatsoft.greenlight.TestSuite
import com.greencatsoft.scales.component.{ Component, inherit, name }

object MacroUtilsTest extends TestSuite {

  "MacroUtils.getAnnotatedValue[A, B]()" should "find an annotation B on type A and return its value" in {

    @name("cool-component")
    class CoolComponent

    val name = MacroUtils.getAnnotatedValue[CoolComponent, name]

    name should be (Some("cool-component"))
  }

  It should "return None when the specified type does not have the given annotation" in {

    class CoolComponent

    val name = MacroUtils.getAnnotatedValue[CoolComponent, name]

    name should be (empty)
  }

  It should "be able to handle multiple annotations" in {

    @name("cool-component")
    @inherit("parent-component")
    class CoolComponent

    val name = MacroUtils.getAnnotatedValue[CoolComponent, name]
    val parent = MacroUtils.getAnnotatedValue[CoolComponent, inherit]

    name should be (Some("cool-component"))
    parent should be (Some("parent-component"))
  }

  It should "be able to read the annotation present on a super class of the given type" in {

    @name("parent-component")
    trait ParentComponent

    class CoolComponent extends ParentComponent

    val name = MacroUtils.getAnnotatedValue[CoolComponent, name]

    name should be (Some("parent-component"))
  }

  It should "read the closest one when multiple annotations are present on the type hierarchy" in {

    @name("parent-component")
    trait ParentComponent

    @name("cool-component")
    class CoolComponent extends ParentComponent

    val name = MacroUtils.getAnnotatedValue[CoolComponent, name]

    name should be (Some("cool-component"))
  }

  "MacroUtils.getPrototype[A]" should "return a name of the prototype object for the specified component" in {

    trait MyComponent extends Component[HTMLInputElement]

    val prototype = MacroUtils.getPrototype[MyComponent]

    prototype should be (Some("HTMLInputElement"))
  }

  It should "resolve a type alias to its base name" in {

    trait MyComponent extends Component[Div]

    val prototype = MacroUtils.getPrototype[MyComponent]

    prototype should be (Some("HTMLDivElement"))
  }

  It should "find the closest parent which is under the 'org.scalajs.dom' package" in {

    trait MyComponent extends Component[MyDiv]

    val prototype = MacroUtils.getPrototype[MyComponent]

    prototype should be (Some("HTMLDivElement"))
  }

  It should "be able to handle parametrized types which do not directly implement the Component trait" in {

    trait BaseComponent[A <: Element] extends Component[A]
    
    trait MyComponent extends BaseComponent[HTMLInputElement]

    val prototype = MacroUtils.getPrototype[MyComponent]

    prototype should be (Some("HTMLInputElement"))
  }

  trait MyDiv extends Div
}