package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.IValidationRule

class SimpleListValidationRuleBuilder[A, B](propertyListExpression: A => List[B],
                                            currentRuleStructure: SimpleValidationRuleStructureContainer[A, B],
                                            validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                            fieldName: String,
                                            previousMappedBuilder: Option[AbstractValidationRuleBuilder[A, _, _]] = None) extends AbstractValidationRuleBuilder[A, List[B], B](propertyListExpression, currentRuleStructure, validationExpressions, fieldName, previousMappedBuilder) {


//  def map[D](valueTransformationFunction: (B) => D) = {
//    val transformingFunction: (A => List[D]) = instance => propertyListExpression(instance).map(valueTransformationFunction)
//    new SimpleListValidationRuleBuilder[A, D](transformingFunction, null, Nil, fieldName, Some(this))
//  }

  protected[validation] override def buildNextInstanceInChain(propertyExpression: A => List[B],
                                                              currentRuleStructure: SimpleValidationRuleStructureContainer[A, B],
                                                              validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                                              fieldName: String,
                                                              previousMappedBuilder: Option[AbstractValidationRuleBuilder[A, _, _]]): AbstractValidationRuleBuilder[A, List[B], B] = {
    new SimpleListValidationRuleBuilder(propertyListExpression, currentRuleStructure, validationExpressions, fieldName, previousMappedBuilder)
  }


  def processRuleStructures(instance: A, ruleStructuresList: List[SimpleValidationRuleStructureContainer[A, B]]): Stream[IValidationRule[A]] = {
    val lazyPropertyListValue = propertyListExpression(instance)
    ruleStructuresList.toStream map {
      ruleStructureContainer =>
        new SimpleListValidationRule[A, B](
          lazyPropertyListValue,
          ruleStructureContainer.validationExpression,
          fieldName,
          ruleStructureContainer.errorMessageBuilder.getOrElse(defaultErrorMessageBuilder),
          ruleStructureContainer.conditionalValidation.getOrElse(defaultConditionedValidation))
    }
  }

}
