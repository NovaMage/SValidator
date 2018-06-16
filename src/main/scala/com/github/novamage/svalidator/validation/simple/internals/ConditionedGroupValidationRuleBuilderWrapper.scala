package com.github.novamage.svalidator.validation.simple.internals

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
