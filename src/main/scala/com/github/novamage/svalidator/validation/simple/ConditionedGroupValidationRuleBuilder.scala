package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.IRuleBuilder

class ConditionedGroupValidationRuleBuilder[A](conditionalExpression: A => Boolean) {

  def apply(ruleBuilder: IRuleBuilder[A]*): IRuleBuilder[A] = {
    new ConditionedGroupValidationRuleBuilderWrapper[A](conditionalExpression, ruleBuilder.toList)
  }
}

class ConditionedGroupValidationRuleBuilderWrapper[A](conditionalExpression: A => Boolean, ruleBuilder: List[IRuleBuilder[A]]) extends IRuleBuilder[A] {

  protected[validation] def buildRules(instance: A): RuleStreamCollection[A] = {
    if (conditionalExpression(instance)) {
      RuleStreamCollection(ruleBuilder.map(_.buildRules(instance)).flatMap(_.ruleStreams))
    } else {
      RuleStreamCollection.empty[A]
    }
  }
}
