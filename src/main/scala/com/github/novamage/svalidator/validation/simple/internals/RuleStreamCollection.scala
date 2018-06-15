package com.github.novamage.svalidator.validation.simple.internals

/** Contains information about validations rules to apply to an instance
  *
  * @tparam A Type object being validated
  */
case class RuleStreamCollection[-A](chains: List[ChainedValidationStream[A]]) {


}


object RuleStreamCollection {

  val Empty = RuleStreamCollection(Nil)
}
