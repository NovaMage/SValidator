package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.IValidationRule

class SimpleOptionValidationRuleBuilder[A, B](propertyOptionExpression: A => Option[B],
                                              currentRuleStructure: SimpleValidationRuleStructureContainer[A, B],
                                              validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                              fieldName: String,
                                              previousMappedBuilder: Option[SimpleValidationRuleBuilder[A, _]] = None) extends AbstractValidationRuleBuilder[A, Option[B], B](propertyOptionExpression, currentRuleStructure, validationExpressions, fieldName) {

  protected[validation] override def buildNextInstanceInChain(propertyExpression: A => Option[B],
                                                              currentRuleStructure: SimpleValidationRuleStructureContainer[A, B],
                                                              validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                                              fieldName: String,
                                                              previousMappedBuilder: Option[AbstractValidationRuleBuilder[A, _, _]] = None): AbstractValidationRuleBuilder[A, Option[B], B] = {
    new SimpleOptionValidationRuleBuilder(propertyOptionExpression, currentRuleStructure, validationExpressions, fieldName)
  }

  protected[validation] def processRuleStructures(instance: A, ruleStructuresList: List[SimpleValidationRuleStructureContainer[A, B]]): Stream[IValidationRule[A]] = {
    lazy val lazyPropertyOptionValue = propertyOptionExpression(instance)
    ruleStructuresList.toStream map {
      ruleStructureContainer =>
        new SimpleOptionValidationRule[A, B](
          lazyPropertyOptionValue,
          ruleStructureContainer.validationExpression,
          fieldName,
          ruleStructureContainer.errorMessageBuilder.getOrElse(defaultErrorMessageBuilder),
          ruleStructureContainer.conditionalValidation.getOrElse(defaultConditionedValidation))
    }
  }
}
