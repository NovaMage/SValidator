package com.github.novamage.svalidator.validation.simple


class SimpleValidationRuleBuilder[A, B](propertyExpression: A => B,
                                        currentRuleStructure: SimpleValidationRuleStructureContainer[A, B],
                                        validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                        fieldName: String) extends AbstractValidationRuleBuilder[A, B, B](propertyExpression, currentRuleStructure, validationExpressions, fieldName) {

  protected[validation] def buildNextInstanceInChain(propertyExpression: (A) => B, currentRuleStructure: SimpleValidationRuleStructureContainer[A, B], validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]], fieldName: String) = {
    new SimpleValidationRuleBuilder(propertyExpression, currentRuleStructure, validationExpressions, fieldName)
  }

  def processRuleStructures(instance: A, ruleStructuresList: List[SimpleValidationRuleStructureContainer[A, B]]): RuleStreamCollection[A] = {
    lazy val propertyValue = propertyExpression(instance)
    val ruleStream = ruleStructuresList.toStream map {
      ruleStructureContainer =>
        new SimpleValidationRule[A, B](
          propertyValue,
          ruleStructureContainer.validationExpression,
          fieldName,
          ruleStructureContainer.errorMessageBuilder.getOrElse(defaultErrorMessageBuilder),
          ruleStructureContainer.conditionalValidation.getOrElse(defaultConditionedValidation))
    }
    RuleStreamCollection(List(ruleStream))
  }
}

