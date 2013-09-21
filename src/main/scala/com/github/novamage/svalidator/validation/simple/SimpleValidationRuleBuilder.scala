package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.IValidationRule


class SimpleValidationRuleBuilder[A, B](propertyExpression: A => B,
                                        currentRuleStructure: SimpleValidationRuleStructureContainer[A, B],
                                        validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                        fieldName: String,
                                        previousMappedBuilder: Option[AbstractValidationRuleBuilder[A, _, _]] = None) extends AbstractValidationRuleBuilder[A, B, B](propertyExpression, currentRuleStructure, validationExpressions, fieldName, previousMappedBuilder) {

  def map[C](valueTransformationFunction: B => C) = {
    val mappedPropertyExpression: (A => C) = instance => valueTransformationFunction(propertyExpression(instance))
    new SimpleValidationRuleBuilder(mappedPropertyExpression, null, Nil, fieldName, Some(this))
  }

  protected[validation] def buildNextInstanceInChain(propertyExpression: (A) => B, currentRuleStructure: SimpleValidationRuleStructureContainer[A, B], validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]], fieldName: String, previousMappedBuilder: Option[AbstractValidationRuleBuilder[A, _, _]]) = {
    new SimpleValidationRuleBuilder(propertyExpression, currentRuleStructure, validationExpressions, fieldName, previousMappedBuilder)
  }

  def processRuleStructures(instance: A, ruleStructuresList: List[SimpleValidationRuleStructureContainer[A, B]]): Stream[IValidationRule[A]] = {
    lazy val propertyValue = propertyExpression(instance)
    ruleStructuresList.toStream map {
      ruleStructureContainer =>
        new SimpleValidationRule[A, B](
          propertyValue,
          ruleStructureContainer.validationExpression,
          fieldName,
          ruleStructureContainer.errorMessageBuilder.getOrElse(defaultErrorMessageBuilder),
          ruleStructureContainer.conditionalValidation.getOrElse(defaultConditionedValidation))
    }
  }
}

