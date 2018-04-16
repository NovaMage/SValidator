package com.github.novamage.svalidator.validation.simple


class SimpleListValidationRuleStarterBuilder[A, B](propertyListExpression: A => List[B],
                                                   currentRuleStructure: SimpleValidationRuleStructureContainer[A, B],
                                                   validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                                   fieldName: String,
                                                   markIndexesOfFieldNameErrors: Boolean) {

  private lazy val notFunctor: ((B, A) => Boolean) => ((B, A) => Boolean) = originalExpression => (propertyValue, instanceValue) => !originalExpression(propertyValue, instanceValue)

  def must(ruleExpressionReceivingPropertyValue: B => Boolean): SimpleListValidationRuleContinuationBuilder[A, B] = {
    val syntheticExpressionWithInstance: (B, A) => Boolean = (property, _) => ruleExpressionReceivingPropertyValue(property)
    addRuleExpressionToList(syntheticExpressionWithInstance)
  }

  private def addRuleExpressionToList(ruleExpression: (B, A) => Boolean): SimpleListValidationRuleContinuationBuilder[A, B] = {
    val ruleList = currentRuleStructure match {
      case null => validationExpressions
      case x => validationExpressions :+ x
    }
    buildNextInstanceInChain(propertyListExpression, SimpleValidationRuleStructureContainer[A, B](ruleExpression, None, None, Map.empty), ruleList, fieldName)
  }

  protected[validation] def buildNextInstanceInChain(propertyExpression: A => List[B],
                                                     currentRuleStructure: SimpleValidationRuleStructureContainer[A, B],
                                                     validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                                     fieldName: String): SimpleListValidationRuleContinuationBuilder[A, B] = {
    new SimpleListValidationRuleContinuationBuilder(propertyListExpression, currentRuleStructure, validationExpressions, fieldName, markIndexesOfFieldNameErrors)
  }

  def mustNot(ruleExpressionReceivingPropertyValue: B => Boolean): SimpleListValidationRuleContinuationBuilder[A, B] = {
    val syntheticExpressionWithInstance: (B, A) => Boolean = (property, _) => ruleExpressionReceivingPropertyValue(property)
    addRuleExpressionToList(applyNotFunctor(syntheticExpressionWithInstance))
  }

  private def applyNotFunctor(expression: (B, A) => Boolean): (B, A) => Boolean = {
    notFunctor(expression)
  }

}
