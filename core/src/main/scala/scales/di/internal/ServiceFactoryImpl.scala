package scales.di.internal

import scala.reflect.macros.blackbox.Context

import scales.component.attribute
import scales.di.Scope
import scales.macros.AnnotationUtils

object ServiceFactoryImpl {

  import AnnotationUtils._

  def newInstance[A](c: Context)(scope: c.Expr[Scope])(implicit tag: c.WeakTypeTag[A]): c.Expr[A] = {
    import c.universe._

    val tpe = tag.tpe

    c.Expr[A] {
      if (tpe.typeSymbol.isAbstract) {
        val name = TypeName(tpe.typeSymbol.name.decodedName.toString + "Impl")

        def isValid(method: MethodSymbol) =
          method.isPublic &&
            method.paramLists.isEmpty &&
            hasMemberAnnotation[attribute](c)(method)

        def find(annotatedOnly: Boolean): PartialFunction[Symbol, Tree] = {
          case m: MethodSymbol if isValid(m) =>
            val name = getMemberAnnotation[attribute](c)(m).map(TermName(_)) getOrElse m.name
            val attr = Literal(Constant(name.decodedName.toString))

            val rt = m.returnType

            if (rt.typeSymbol.fullName == "scala.Option") {
              val t = rt.typeArgs.head

              q"""override def ${m.name}: Option[$t] = getAttribute[$t]($attr)"""
            } else {
              if (m.isAbstract) {
                q"""
                  override def ${m.name}: $rt = getAttribute[$rt]($attr) getOrElse {
                    val name = $attr
                    throw new IllegalArgumentException(s"Unable to find an attribute with a name '$$name'.")
                  }
                """
              } else {
                q"""override def ${m.name}: $rt = getAttribute[$rt]($attr) getOrElse super.${m.name}"""
              }
            }
        }

        val overrides = tag.tpe.members collect find(true)

        q"""
          class $name extends $tpe {
           ..$overrides
          }

          new $name
        """
      } else {
        q"new $tpe"
      }
    }
  }
}