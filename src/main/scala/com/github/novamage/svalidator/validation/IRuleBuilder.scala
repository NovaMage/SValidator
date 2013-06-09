package com.github.novamage.svalidator.validation

trait IRuleBuilder[-T] {

  protected[validation] def buildRules: List[IValidationRule[T]]
}
