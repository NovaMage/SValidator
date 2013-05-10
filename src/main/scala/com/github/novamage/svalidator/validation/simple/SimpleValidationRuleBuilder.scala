package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.IRuleBuilder

class SimpleValidationRuleBuilder[A, B](propertyExpression: A => B,
                                        currentRuleStructure: SimpleValidationRuleStructureContainer[A, B],
                                        validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                        fieldName: String)
  extends IRuleBuilder[A] {

  def when(conditionedValidation: A => Boolean) = {
    new SimpleValidationRuleBuilder(propertyExpression, currentRuleStructure.copy(conditionalValidation = Some(conditionedValidation)), validationExpressions, fieldName)
  }

  def must(ruleExpression: B => Boolean) = {
    val ruleList = currentRuleStructure match {
      case null => validationExpressions
      case x => validationExpressions :+ x
    }
    new SimpleValidationRuleBuilder(propertyExpression, SimpleValidationRuleStructureContainer[A, B](ruleExpression, None, None), ruleList, fieldName)
  }

  def withMessage(aFormatStringReceivingFieldNameAndValue: String) = {
    val errorMessageAlternateBuilder: ((String, B) => String) = (fieldName, fieldValue) => aFormatStringReceivingFieldNameAndValue.format(fieldName, fieldValue)
    new SimpleValidationRuleBuilder(propertyExpression, currentRuleStructure.copy(errorMessageBuilder = Some(errorMessageAlternateBuilder)), validationExpressions, fieldName)
  }

  def withMessage(anExpressionReceivingFieldValue: B => String) = {
    val errorMessageAlternateBuilder: ((String, B) => String) = (fieldName, value) => anExpressionReceivingFieldValue(value)
    new SimpleValidationRuleBuilder(propertyExpression, currentRuleStructure.copy(errorMessageBuilder = Some(errorMessageAlternateBuilder)), validationExpressions, fieldName)
  }

  def withMessage(expressionReceivingFieldNameAndValue: (String, B) => String) = {
    new SimpleValidationRuleBuilder(propertyExpression, currentRuleStructure.copy(errorMessageBuilder = Some(expressionReceivingFieldNameAndValue)), validationExpressions, fieldName)
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
        new SimpleValidationRule[A, B](
          propertyExpression,
          ruleStructureContainer.validationExpression,
          fieldName,
          ruleStructureContainer.errorMessageBuilder.getOrElse(defaultErrorMessageBuilder),
          ruleStructureContainer.conditionalValidation.getOrElse(defaultConditionedValidation))
    }
  }
}

