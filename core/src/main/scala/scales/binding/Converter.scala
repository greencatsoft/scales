package scales.binding

import scala.util.Try

trait Converter[A] extends Function1[String, Try[A]]
