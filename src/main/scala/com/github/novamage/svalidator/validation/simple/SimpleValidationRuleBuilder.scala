package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.IRuleBuilder

class SimpleValidationRuleBuilder[A, B](propertyExpression: A => B, validationExpressions: List[B => Boolean], fieldName: String, errorMessages: List[(String, B) => String], conditionedValidation: A => Boolean) extends IRuleBuilder[A] {

  def When(conditionedValidation: A => Boolean) = {
    new SimpleValidationRuleBuilder(propertyExpression, validationExpressions, fieldName, errorMessages, conditionedValidation)
  }

  def Must(ruleExpression: B => Boolean) = {
    new SimpleValidationRuleBuilder(propertyExpression, validationExpressions :+ ruleExpression, fieldName, errorMessages, conditionedValidation)
  }

  def WithMessage(errorStringWithPrintfFormat: String) = {
    val errorMessageAlternateBuilder: ((String, B) => String) = (fieldName, fieldValue) => errorStringWithPrintfFormat.format(fieldName, fieldValue)
    new SimpleValidationRuleBuilder(propertyExpression, validationExpressions, fieldName, errorMessages :+ errorMessageAlternateBuilder, conditionedValidation)
  }

  def WithMessage(errorMessageBuilder: B => String) = {
    val errorMessageAlternateBuilder: ((String, B) => String) = (fieldName, value) => errorMessageBuilder(value)
    new SimpleValidationRuleBuilder(propertyExpression, validationExpressions, fieldName, errorMessages :+ errorMessageAlternateBuilder, conditionedValidation)
  }

  def WithMessage(errorMessageBuilder: (String, B) => String) = {
    new SimpleValidationRuleBuilder(propertyExpression, validationExpressions, fieldName, errorMessages :+ errorMessageBuilder, conditionedValidation)
  }

  def buildRules = {
    val defaultErrorMessageBuilder: ((String, B) => String) = (fieldName, fieldValue) => s"$fieldValue is not a valid value for $fieldName"
    validationExpressions.zipWithIndex.map {
      case (ruleExpression, index) => new SimpleValidationRule(propertyExpression, ruleExpression, fieldName, if (index < errorMessages.size) errorMessages(index) else defaultErrorMessageBuilder, conditionedValidation)
    }
  }
}

