package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.simple.internals.{RuleBuilder, RuleStreamCollection}

class ConditionedGroupValidationRuleBuilder[A](conditionalExpression: A => Boolean) {

  def apply(ruleBuilder: RuleBuilder[A]*): RuleBuilder[A] = {
    new ConditionedGroupValidationRuleBuilderWrapper[A](conditionalExpression, ruleBuilder.toList)
  }
}

class ConditionedGroupValidationRuleBuilderWrapper[A](conditionalExpression: A => Boolean, ruleBuilders: List[RuleBuilder[A]]) extends RuleBuilder[A] {


  protected[validation] def buildRules(instance: A): RuleStreamCollection[A] = {
    if (conditionalExpression(instance)) {
      val ruleStreamCollections = ruleBuilders.map(_.buildRules(instance))
      RuleStreamCollection(ruleStreamCollections.flatMap(_.chains))
    } else {
      RuleStreamCollection.Empty
    }
  }
}
