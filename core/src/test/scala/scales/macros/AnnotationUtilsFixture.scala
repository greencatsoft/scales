package scales.macros

import scala.language.experimental.macros
import scala.annotation.StaticAnnotation
import scala.reflect.macros.blackbox.Context

object AnnotationUtilsFixture {

  def getValue[A, B <: StaticAnnotation](): Option[String] = macro getValueImpl[A, B]

  def getValueImpl[A, B <: StaticAnnotation](c: Context)()(
    implicit targetTag: c.WeakTypeTag[A], annotationTag: c.WeakTypeTag[B]): c.Expr[Option[String]] = {
    AnnotationUtils.getValueExpr[B](c)(targetTag)
  }
}