package com.greencatsoft.scales.dom

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal

import com.greencatsoft.greenlight.TestSuite

import com.greencatsoft.scales.dom.Observer.asObservable

object ObserverTest extends TestSuite {

  "Object.observe()" should "observe properties of the given js.Dynamic instance and notify changes" in {
    val target: js.Dynamic = literal("property" -> 1)

    val callback: js.Function1[js.Array[Change], Unit] = (changes: js.Array[Change]) => {
      changes should not be (empty)

      changes.headOption.foreach { change =>
        change.name should be ("property")
        change.`type` should be ("update")
        change.oldValue should be (1)
      }
    }

    js.Object.observe(target, callback)

    target.property = 2
    js.Object.deliverChangeRecords(callback)
  }

  "Object.unobserve()" should "unregister the specified handler so that it stop being notified about the changes" in {
    val target: js.Dynamic = literal("property" -> 1)

    var changed = false

    val callback: js.Function1[js.Array[Change], Unit] = (changes: js.Array[Change]) => {
      changed = true
    }

    js.Object.observe(target, callback)
    js.Object.unobserve(target, callback)

    target.property = 2
    js.Object.deliverChangeRecords(callback)

    changed should be (false)
  }
}