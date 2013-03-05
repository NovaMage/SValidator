package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.IRuleBuilder
import constructs.{ HaveConstruct, BeConstruct }

class SimpleValidationRuleBuilder[A, B](
  propertyExpression: A => B,
  validationExpressions: List[B => Boolean],
  fieldName: String,
  errorMessages: List[(String, B) => String],
  conditionedValidation: A => Boolean)
    extends IRuleBuilder[A] {

  def when(conditionedValidation: A => Boolean) = {
    new SimpleValidationRuleBuilder(propertyExpression, validationExpressions, fieldName, errorMessages, conditionedValidation)
  }

  def must(ruleExpression: B => Boolean) = {
    new SimpleValidationRuleBuilder(propertyExpression, validationExpressions :+ ruleExpression, fieldName, errorMessages, conditionedValidation)
  }

  def mustNot(ruleExpression: B => Boolean) = {
    val notFunctor: (B => Boolean) => (B => Boolean) = originalExpression => parameter => !originalExpression(parameter)
    new SimpleValidationRuleBuilder(propertyExpression, validationExpressions :+ notFunctor(ruleExpression), fieldName, errorMessages, conditionedValidation)
  }

  def withMessage(aFormatStringReceivingFieldNameAndValue: String) = {
    val errorMessageAlternateBuilder: ((String, B) => String) = (fieldName, fieldValue) => aFormatStringReceivingFieldNameAndValue.format(fieldName, fieldValue)
    new SimpleValidationRuleBuilder(propertyExpression, validationExpressions, fieldName, errorMessages :+ errorMessageAlternateBuilder, conditionedValidation)
  }

  def withMessage(anExpressionReceivingFieldValue: B => String) = {
    val errorMessageAlternateBuilder: ((String, B) => String) = (fieldName, value) => anExpressionReceivingFieldValue(value)
    new SimpleValidationRuleBuilder(propertyExpression, validationExpressions, fieldName, errorMessages :+ errorMessageAlternateBuilder, conditionedValidation)
  }

  def withMessage(expressionReceivingFieldNameAndValue: (String, B) => String) = {
    new SimpleValidationRuleBuilder(
      propertyExpression,
      validationExpressions,
      fieldName,
      errorMessages :+ expressionReceivingFieldNameAndValue,
      conditionedValidation)
  }

  protected[validation] override def buildRules = {
    val defaultErrorMessageBuilder: ((String, B) => String) = (fieldName, fieldValue) => s"$fieldValue is not a valid value for $fieldName"
    validationExpressions.zipWithIndex.map {
      case (ruleExpression, index) => new SimpleValidationRule(propertyExpression, ruleExpression, fieldName, if (index < errorMessages.size) errorMessages(index) else defaultErrorMessageBuilder, conditionedValidation)
    }
  }
}

