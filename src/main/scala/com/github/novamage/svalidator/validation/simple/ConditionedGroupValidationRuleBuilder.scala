package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.{IValidationRule, IRuleBuilder}

class ConditionedGroupValidationRuleBuilder[A](conditionalExpression: A => Boolean) {

  def apply(ruleBuilder: IRuleBuilder[A]): IRuleBuilder[A] = {
    new ConditionedGroupValidationRuleBuilderWrapper[A](conditionalExpression, ruleBuilder)
  }
}

class ConditionedGroupValidationRuleBuilderWrapper[A](conditionalExpression: A => Boolean, ruleBuilder: IRuleBuilder[A]) extends IRuleBuilder[A] {

  protected[validation] def buildRules(instance: A): Stream[IValidationRule[A]] = {
    if (conditionalExpression(instance)) {
      ruleBuilder.buildRules(instance)
    } else {
      Stream.Empty
    }
  }
}
