package scales.di

trait ServiceFactory {

  def newInstance[A](scope: Scope): A
}

object ServiceFactory extends ServiceFactory {

  override def newInstance[A](scope: Scope): A = ???
}