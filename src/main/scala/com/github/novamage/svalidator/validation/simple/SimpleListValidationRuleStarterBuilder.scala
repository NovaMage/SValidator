package com.github.novamage.svalidator.validation.simple


class SimpleListValidationRuleStarterBuilder[A, B, +C](propertyListExpression: A => List[B],
                                                       currentRuleStructure: Option[SimpleValidationRuleStructureContainer[A, B]],
                                                       validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                                       fieldName: String,
                                                       markIndexesOfFieldNameErrors: Boolean,
                                                       previousMappedBuilderInChain: Option[IRuleBuilder[A]],
                                                       previousMappedBuilderValueProvider: Option[UpstreamLazyValueProvider[List[C]]],
                                                       previousMappedBuilderValueConverter: Option[C => B]) {


  private lazy val notFunctor: ((B, A) => Boolean) => (B, A) => Boolean = originalExpression => (propertyValue, instanceValue) => !originalExpression(propertyValue, instanceValue)

  protected[simple] def negated: SimpleListValidationRuleStarterBuilder[A, B, C] = {
    new SimpleListValidationRuleStarterBuilder(propertyListExpression,
      currentRuleStructure,
      validationExpressions,
      fieldName,
      markIndexesOfFieldNameErrors,
      previousMappedBuilderInChain,
      previousMappedBuilderValueProvider,
      previousMappedBuilderValueConverter) {

      override def must(ruleExpressionReceivingPropertyValue: B => Boolean): SimpleListValidationRuleContinuationBuilder[A, B, C] = {
        super.mustNot(ruleExpressionReceivingPropertyValue)
      }

      override def mustNot(ruleExpressionReceivingPropertyValue: B => Boolean): SimpleListValidationRuleContinuationBuilder[A, B, C] = {
        super.must(ruleExpressionReceivingPropertyValue)
      }

    }
  }

  def must(ruleExpressionReceivingPropertyValue: B => Boolean): SimpleListValidationRuleContinuationBuilder[A, B, C] = {
    val syntheticExpressionWithInstance: (B, A) => Boolean = (property, _) => ruleExpressionReceivingPropertyValue(property)
    addRuleExpressionToList(syntheticExpressionWithInstance)
  }

  def mustNot(ruleExpressionReceivingPropertyValue: B => Boolean): SimpleListValidationRuleContinuationBuilder[A, B, C] = {
    val syntheticExpressionWithInstance: (B, A) => Boolean = (property, _) => ruleExpressionReceivingPropertyValue(property)
    addRuleExpressionToList(applyNotFunctor(syntheticExpressionWithInstance))
  }

  private def addRuleExpressionToList(ruleExpression: (B, A) => Boolean): SimpleListValidationRuleContinuationBuilder[A, B, C] = {
    val ruleList = currentRuleStructure match {
      case None => validationExpressions
      case Some(ruleStructure) => validationExpressions ::: ruleStructure :: Nil
    }
    buildNextInstanceInChain(propertyListExpression, Some(SimpleValidationRuleStructureContainer[A, B](ruleExpression, None, None, None, Map.empty)), ruleList, fieldName)
  }

  protected[validation] def buildNextInstanceInChain(propertyExpression: A => List[B],
                                                     currentRuleStructure: Option[SimpleValidationRuleStructureContainer[A, B]],
                                                     validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                                     fieldName: String): SimpleListValidationRuleContinuationBuilder[A, B, C] = {
    new SimpleListValidationRuleContinuationBuilder(propertyListExpression,
      currentRuleStructure,
      validationExpressions,
      fieldName,
      markIndexesOfFieldNameErrors,
      previousMappedBuilderInChain,
      previousMappedBuilderValueProvider,
      previousMappedBuilderValueConverter)
  }

  private def applyNotFunctor(expression: (B, A) => Boolean): (B, A) => Boolean = {
    notFunctor(expression)
  }

}
