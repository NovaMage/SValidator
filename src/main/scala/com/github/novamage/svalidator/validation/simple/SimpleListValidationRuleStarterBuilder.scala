package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.IRuleBuilder


class SimpleListValidationRuleStarterBuilder[A, B](propertyListExpression: A => List[B],
                                                   currentRuleStructure: Option[SimpleValidationRuleStructureContainer[A, B]],
                                                   validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                                   fieldName: String,
                                                   markIndexesOfFieldNameErrors: Boolean,
                                                   previousMappedBuilderInChain: Option[IRuleBuilder[A]]) {

  private lazy val notFunctor: ((B, A) => Boolean) => ((B, A) => Boolean) = originalExpression => (propertyValue, instanceValue) => !originalExpression(propertyValue, instanceValue)

  def must(ruleExpressionReceivingPropertyValue: B => Boolean): SimpleListValidationRuleContinuationBuilder[A, B] = {
    val syntheticExpressionWithInstance: (B, A) => Boolean = (property, _) => ruleExpressionReceivingPropertyValue(property)
    addRuleExpressionToList(syntheticExpressionWithInstance)
  }

  private def addRuleExpressionToList(ruleExpression: (B, A) => Boolean): SimpleListValidationRuleContinuationBuilder[A, B] = {
    val ruleList = currentRuleStructure match {
      case None => validationExpressions
      case Some(ruleStructure) => validationExpressions :+ ruleStructure
    }
    buildNextInstanceInChain(propertyListExpression, Some(SimpleValidationRuleStructureContainer[A, B](ruleExpression, None, None, Map.empty)), ruleList, fieldName)
  }

  protected[validation] def buildNextInstanceInChain(propertyExpression: A => List[B],
                                                     currentRuleStructure: Option[SimpleValidationRuleStructureContainer[A, B]],
                                                     validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                                     fieldName: String): SimpleListValidationRuleContinuationBuilder[A, B] = {
    new SimpleListValidationRuleContinuationBuilder(propertyListExpression, currentRuleStructure, validationExpressions, fieldName, markIndexesOfFieldNameErrors, previousMappedBuilderInChain)
  }

  def mustNot(ruleExpressionReceivingPropertyValue: B => Boolean): SimpleListValidationRuleContinuationBuilder[A, B] = {
    val syntheticExpressionWithInstance: (B, A) => Boolean = (property, _) => ruleExpressionReceivingPropertyValue(property)
    addRuleExpressionToList(applyNotFunctor(syntheticExpressionWithInstance))
  }

  private def applyNotFunctor(expression: (B, A) => Boolean): (B, A) => Boolean = {
    notFunctor(expression)
  }

}
