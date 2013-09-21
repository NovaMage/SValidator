package com.github.novamage.svalidator.validation

trait IRuleBuilder[-A] {

  protected[validation] def buildRules(instance: A): Stream[IValidationRule[A]]
}
