package scales.di.internal

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

import scales.di.Scope

object ServiceFactoryImpl {

  def newInstance[A](c: Context)(scope: c.Expr[Scope])(implicit tag: c.WeakTypeTag[A]): c.Expr[A] = {
    import c.universe._

    val tpe = tag.tpe

    val overrides: List[DefDef] = Nil

    c.Expr[A] {
      if (tpe.typeSymbol.isAbstract) {
        val name = TypeName(tpe.typeSymbol.name.decodedName.toString + "Impl")

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