package com.github.novamage.svalidator.validation.simple.internals

class LazyValueContainer[A](value: => A) {

  def extractValue: A = value

}
