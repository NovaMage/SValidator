package com.github.novamage.svalidator.validation.simple

class SimpleOptionValidationRuleBuilder[A, B](propertyOptionExpression: A => Option[B],
                                              currentRuleStructure: SimpleValidationRuleStructureContainer[A, B],
                                              validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                              fieldName: String) extends SimpleValidationRuleBuilder[A, B](null, currentRuleStructure, validationExpressions, fieldName) {

  protected[validation] override def buildNextInstanceInChain(propertyExpression: A => B,
                                                              currentRuleStructure: SimpleValidationRuleStructureContainer[A, B],
                                                              validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                                              fieldName: String): SimpleValidationRuleBuilder[A, B] = {
    new SimpleOptionValidationRuleBuilder(propertyOptionExpression, currentRuleStructure, validationExpressions, fieldName)
  }

  protected[validation] override def buildRules = {
    val ruleStructures = currentRuleStructure match {
      case null => validationExpressions
      case x => validationExpressions :+ x
    }
    val defaultErrorMessageBuilder: ((String, B) => String) = (fieldName, fieldValue) => s"$fieldValue is not a valid value for $fieldName"
    val defaultConditionedValidation: A => Boolean = x => true
    ruleStructures map {
      ruleStructureContainer =>
        new SimpleOptionValidationRule[A, B](
          propertyOptionExpression,
          ruleStructureContainer.validationExpression,
          fieldName,
          ruleStructureContainer.errorMessageBuilder.getOrElse(defaultErrorMessageBuilder),
          ruleStructureContainer.conditionalValidation.getOrElse(defaultConditionedValidation))
    }
  }

}
