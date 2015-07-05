package com.greencatsoft.scales.component

import scala.annotation.StaticAnnotation

trait ComponentAnnotation extends StaticAnnotation

trait PropertyAnnotation extends StaticAnnotation

class name(name: String) extends ComponentAnnotation

class inherit(name: String) extends ComponentAnnotation

class property(name: String) extends PropertyAnnotation

class enumerable extends PropertyAnnotation