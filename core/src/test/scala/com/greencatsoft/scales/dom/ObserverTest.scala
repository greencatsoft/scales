package com.greencatsoft.scales.dom

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js.annotation.{ JSExport, JSExportAll }

import com.greencatsoft.greenlight.TestSuite

import ImplicitConversions.asObservable

object ObserverTest extends TestSuite {

  "Object.observe()" should "observe properties of the given js.Dynamic instance and notify changes" in {
    val target: js.Dynamic = literal("intValue" -> 1, "stringValue" -> "a")

    var changes: Seq[ChangeEvent] = Nil
    val callback: Callback = (c: js.Array[ChangeEvent]) => changes = c

    js.Object.observe(target, callback)

    target.intValue = 2

    js.Object.deliverChangeRecords(callback)

    changes should not be (empty)

    changes.headOption foreach { change =>
      change.name should be ("intValue")
      change.`type` should be ("update")
      change.oldValue should be (1)
    }

    changes = Nil

    target.stringValue = "b"

    js.Object.deliverChangeRecords(callback)

    changes should not be (empty)

    changes.headOption foreach { change =>
      change.name should be ("stringValue")
      change.`type` should be ("update")
      change.oldValue should be ("a")
    }
  }

  It should "be able to observe exported properties from a Scala.js object instance" in {
    val target = new ObserverTestFixture

    var changes: Seq[ChangeEvent] = Nil
    val callback: Callback = (c: js.Array[ChangeEvent]) => changes = c

    js.Object.observe(target, callback)

    target.intValue = 2

    js.Object.deliverChangeRecords(callback)

    changes should not be (empty)

    changes.headOption foreach { change =>
      change.name should be ("intValue$1")
      change.`type` should be ("update")
      change.oldValue should be (1)
    }

    changes = Nil

    target.stringValue = "b"

    js.Object.deliverChangeRecords(callback)

    changes should not be (empty)

    changes.headOption foreach { change =>
      change.name should be ("stringValue$1")
      change.`type` should be ("update")
      change.oldValue should be ("a")
    }
  }

  "Object.unobserve()" should "unregister the specified handler so that it stop being notified about the changes" in {
    val target: js.Dynamic = literal("property" -> 1)

    var changes: Seq[ChangeEvent] = Nil
    val callback: Callback = (c: js.Array[ChangeEvent]) => changes = c

    js.Object.observe(target, callback)
    js.Object.unobserve(target, callback)

    target.property = 2

    js.Object.deliverChangeRecords(callback)

    changes should be (empty)
  }

  type Callback = js.Function1[js.Array[ChangeEvent], Unit]
}

@JSExport
@JSExportAll
class ObserverTestFixture(var intValue: Int = 1, var stringValue: String = "a")