package com.github.novamage.svalidator.validation.simple

class ConditionedGroupValidationRuleBuilder[A](conditionalExpression: A => Boolean) {

  def apply(ruleBuilder: IRuleBuilder[A]*): IRuleBuilder[A] = {
    new ConditionedGroupValidationRuleBuilderWrapper[A](conditionalExpression, ruleBuilder.toList)
  }
}

class ConditionedGroupValidationRuleBuilderWrapper[A](conditionalExpression: A => Boolean, ruleBuilders: List[IRuleBuilder[A]]) extends IRuleBuilder[A] {


  protected[validation] def buildRules(instance: A): RuleStreamCollection[A] = {
    if (conditionalExpression(instance)) {
      val ruleStreamCollections = ruleBuilders.map(_.buildRules(instance))
      RuleStreamCollection(ruleStreamCollections.flatMap(_.chains))
    } else {
      RuleStreamCollection.Empty
    }
  }
}
