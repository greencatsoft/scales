package com.greencatsoft.scales.component.internal

import scala.scalajs.js.annotation.{ JSExport, JSExportAll }

import org.scalajs.dom.Element
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.{ HTMLElement, HTMLInputElement }

import com.greencatsoft.greenlight.TestSuite
import com.greencatsoft.scales.component.{ Component, enumerable }

object ComponentRegistryImplTest extends TestSuite {

  import ComponentRegistryImplFixture._

  "ComponentRegistryImpl.getPrototype[A]" should "return a name of the prototype object for the specified component" in {
    class MyComponent extends Component[HTMLInputElement]

    val prototype = getPrototype[MyComponent]

    prototype should be (Some("HTMLInputElement"))
  }

  It should "resolve a type alias to its base name" in {
    class MyComponent extends Component[Div]

    val prototype = getPrototype[MyComponent]

    prototype should be (Some("HTMLDivElement"))
  }

  It should "find the closest parent which is under the 'org.scalajs.dom' package" in {
    class MyComponent extends Component[HTMLElement]

    val prototype = getPrototype[MyComponent]

    prototype should be (Some("HTMLElement"))
  }

  It should "be able to handle parametrized types which do not directly implement the Component trait" in {
    class BaseComponent[A <: Element] extends Component[A]

    class MyComponent extends BaseComponent[HTMLInputElement]

    val prototype = getPrototype[MyComponent]

    prototype should be (Some("HTMLInputElement"))
  }

  "ComponentRegistryImpl.getProperties[A]" should "return a collection of property definitions of the specified component" in {
    class MyComponent extends Component[Div] {

      @JSExport
      val propertyA = 1

      @JSExport
      @enumerable
      var propertyB = "test"
    }

    val properties = getProperties[MyComponent]().sortBy(_.name)

    properties.size should be (2)

    properties.headOption foreach { p =>
      p.name should be ("propertyA")
      p.readOnly should be (true)
      p.enumerable should be (false)
    }

    properties.lastOption foreach { p =>
      p.name should be ("propertyB")
      p.readOnly should be (false)
      p.enumerable should be (true)
    }
  }

  It should "ignore any non-public properties" in {
    class MyComponent extends Component[Div] {

      @JSExport
      val propertyA = 1

      @JSExport
      protected var propertyB = "test"
    }

    val properties = getProperties[MyComponent]().sortBy(_.name)

    properties.size should be (1)

    properties.headOption foreach { p =>
      p.name should be ("propertyA")
      p.readOnly should be (true)
      p.enumerable should be (false)
    }
  }

  It should "ignore properties without @JSExport annotation, if not otherwise exported" in {
    class MyComponent extends Component[Div] {

      val propertyA = 1

      @JSExport
      var propertyB = "test"
    }

    val properties = getProperties[MyComponent]().sortBy(_.name)

    properties.size should be (1)

    properties.headOption foreach { p =>
      p.name should be ("propertyB")
      p.readOnly should be (false)
      p.enumerable should be (false)
    }
  }

  It should "include properties without @JSExport annotation, if the enclosing class is annotated with @JSExportAll" in {
    @JSExportAll
    class MyComponent extends Component[Div] {

      val propertyA = 1

      @enumerable
      var propertyB = "test"
    }

    val properties = getProperties[MyComponent]().sortBy(_.name)

    properties.size should be (2)

    properties.headOption foreach { p =>
      p.name should be ("propertyA")
      p.readOnly should be (true)
      p.enumerable should be (false)
    }

    properties.lastOption foreach { p =>
      p.name should be ("propertyB")
      p.readOnly should be (false)
      p.enumerable should be (true)
    }
  }

  It should "include properties inherited from the ancestors of the specified type" in {
    class BaseComponent[A <: Element] extends Component[A] {
      @JSExport
      @enumerable
      val propertyA = 1

      @JSExport
      protected var propertyC = "test"
    }

    class MyComponent extends BaseComponent[HTMLInputElement] {
      @JSExport
      var propertyB = "test"

      val propertyD = false
    }

    val properties = getProperties[MyComponent]().sortBy(_.name)

    properties.size should be (2)

    properties.headOption foreach { p =>
      p.name should be ("propertyA")
      p.readOnly should be (true)
      p.enumerable should be (true)
    }

    properties.lastOption foreach { p =>
      p.name should be ("propertyB")
      p.readOnly should be (false)
      p.enumerable should be (false)
    }
  }
}