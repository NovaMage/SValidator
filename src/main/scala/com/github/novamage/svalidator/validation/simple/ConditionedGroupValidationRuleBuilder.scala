package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.simple.internals.{ConditionedGroupValidationRuleBuilderWrapper, RuleBuilder, RuleStreamCollection}

/** Chain builder that only executes all its internal builders if the passed in condition evaluates to true
  *
  * @param conditionalExpression Condition to apply to the instance
  * @tparam A Type of the instance being validated
  */
class ConditionedGroupValidationRuleBuilder[A](conditionalExpression: A => Boolean) {

  /** Returns a builder containing all the passed in ruleBuilders that will only be executed if the conditional expression
    * evaluates to true
    *
    * @param ruleBuilder Rule builders that will be conditioned
    */
  def apply(ruleBuilder: RuleBuilder[A]*): RuleBuilder[A] = {
    new ConditionedGroupValidationRuleBuilderWrapper[A](conditionalExpression, ruleBuilder.toList)
  }
}