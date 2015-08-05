package scales.di

object GlobalScope extends Scope {

  override def parent: Option[Scope] = None

  override def doResolve[A](name: Option[String]): Option[A] = ???
}