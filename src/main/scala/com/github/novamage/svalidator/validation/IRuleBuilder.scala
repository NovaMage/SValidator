package com.github.novamage.svalidator.validation

trait IRuleBuilder[T] {

  def buildRules : List[IValidationRule[T]]
}
