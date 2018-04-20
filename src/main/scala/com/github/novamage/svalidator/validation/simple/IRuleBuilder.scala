package com.github.novamage.svalidator.validation.simple

trait IRuleBuilder[-A] {

  protected[validation] def buildRules(instance: A): RuleStreamCollection[A]
}
