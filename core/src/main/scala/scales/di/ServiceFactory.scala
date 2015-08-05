package scales.di

import scala.language.experimental.macros

import internal.ServiceFactoryImpl

trait ServiceFactory {

  def newInstance[A](scope: Scope): A
}

object ServiceFactory {

  def newInstance[A](scope: Scope): A = macro ServiceFactoryImpl.newInstance[A]
}