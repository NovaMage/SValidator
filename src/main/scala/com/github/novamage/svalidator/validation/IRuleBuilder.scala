package com.github.novamage.svalidator.validation

import com.github.novamage.svalidator.validation.simple.RuleStreamCollection

trait IRuleBuilder[-A] {

  protected[validation] def buildRules(instance: A): RuleStreamCollection[A]
}
