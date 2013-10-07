package com.github.novamage.svalidator.validation.simple


class SimpleListValidationRuleBuilder[A, B](propertyListExpression: A => List[B],
                                            currentRuleStructure: SimpleValidationRuleStructureContainer[A, B],
                                            validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                            fieldName: String,
                                            markIndexesOfFieldNameErrors: Boolean) extends AbstractValidationRuleBuilder[A, List[B], B](propertyListExpression, currentRuleStructure, validationExpressions, fieldName) {


  protected[validation] override def buildNextInstanceInChain(propertyExpression: A => List[B],
                                                              currentRuleStructure: SimpleValidationRuleStructureContainer[A, B],
                                                              validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                                              fieldName: String): AbstractValidationRuleBuilder[A, List[B], B] = {
    new SimpleListValidationRuleBuilder(propertyListExpression, currentRuleStructure, validationExpressions, fieldName, markIndexesOfFieldNameErrors)
  }


  def processRuleStructures(instance: A, ruleStructuresList: List[SimpleValidationRuleStructureContainer[A, B]]): RuleStreamCollection[A] = {
    lazy val lazyPropertyListValue = propertyListExpression(instance)
    val ruleStream = ruleStructuresList.toStream map {
      ruleStructureContainer =>
        new SimpleListValidationRule[A, B](
          lazyPropertyListValue,
          ruleStructureContainer.validationExpression,
          fieldName,
          ruleStructureContainer.errorMessageBuilder.getOrElse(defaultErrorMessageBuilder),
          ruleStructureContainer.conditionalValidation.getOrElse(defaultConditionedValidation),
          markIndexesOfFieldNameErrors)
    }
    RuleStreamCollection(List(ruleStream))
  }

}
