package com.github.novamage.svalidator.validation.simple

class LazyValueContainer[A](value: => A) {

  def extractValue: A = value

}
