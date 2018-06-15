package com.github.novamage.svalidator.validation.simple.internals

/** Specialized base class for all constructs that permit a fluent style of validation
  *
  * @tparam A Type of object being validated
  */
trait RuleBuilder[-A] {

  protected[validation] def buildRules(instance: A): RuleStreamCollection[A]
}
