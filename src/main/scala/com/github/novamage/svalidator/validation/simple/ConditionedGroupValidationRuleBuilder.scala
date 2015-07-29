package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.utils.Utils
import com.github.novamage.svalidator.validation.IRuleBuilder

class ConditionedGroupValidationRuleBuilder[A](conditionalExpression: A => Boolean) {

  def apply(ruleBuilder: IRuleBuilder[A]*): IRuleBuilder[A] = {
    new ConditionedGroupValidationRuleBuilderWrapper[A](conditionalExpression, ruleBuilder.toList)
  }
}

class ConditionedGroupValidationRuleBuilderWrapper[A](conditionalExpression: A => Boolean, ruleBuilders: List[IRuleBuilder[A]]) extends IRuleBuilder[A] {


  protected[validation] def buildRules(instance: A): RuleStreamCollection[A] = {
    if (conditionalExpression(instance)) {
      val ruleStreamCollections = ruleBuilders.map(_.buildRules(instance))
      RuleStreamCollection(ruleStreamCollections.flatMap(_.ruleStreams), ruleStreamCollections.map(_.metadata).reduce(Utils.mergeMaps))
    } else {
      RuleStreamCollection.Empty
    }
  }
}
