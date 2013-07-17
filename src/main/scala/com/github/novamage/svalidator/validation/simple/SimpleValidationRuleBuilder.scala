package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.{IValidationRule, IRuleBuilder}

class SimpleValidationRuleBuilder[A, B](propertyExpression: A => B,
                                        currentRuleStructure: SimpleValidationRuleStructureContainer[A, B],
                                        validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                        fieldName: String)
  extends IRuleBuilder[A] {

  def when(conditionedValidation: A => Boolean) = {
    buildNextInstanceInChain(propertyExpression, currentRuleStructure.copy(conditionalValidation = Some(conditionedValidation)), validationExpressions, fieldName)
  }

  def must(ruleExpression: B => Boolean): SimpleValidationRuleBuilder[A, B] = {
    addRuleExpressionToList(ruleExpression)
  }


  def mustNot(ruleExpression: B => Boolean): SimpleValidationRuleBuilder[A, B] = {
    addRuleExpressionToList(applyNotFunctor(ruleExpression))
  }

  def withMessage(aFormatStringReceivingFieldNameAndValue: String): SimpleValidationRuleBuilder[A, B] = {
    val errorMessageAlternateBuilder: ((String, B) => String) = (fieldName, fieldValue) => aFormatStringReceivingFieldNameAndValue.format(fieldName, fieldValue)
    buildNextInstanceInChain(propertyExpression, currentRuleStructure.copy(errorMessageBuilder = Some(errorMessageAlternateBuilder)), validationExpressions, fieldName)
  }

  def withMessage(anExpressionReceivingFieldValue: B => String): SimpleValidationRuleBuilder[A, B] = {
    val errorMessageAlternateBuilder: ((String, B) => String) = (fieldName, value) => anExpressionReceivingFieldValue(value)
    buildNextInstanceInChain(propertyExpression, currentRuleStructure.copy(errorMessageBuilder = Some(errorMessageAlternateBuilder)), validationExpressions, fieldName)
  }

  def withMessage(expressionReceivingFieldNameAndValue: (String, B) => String): SimpleValidationRuleBuilder[A, B] = {
    buildNextInstanceInChain(propertyExpression, currentRuleStructure.copy(errorMessageBuilder = Some(expressionReceivingFieldNameAndValue)), validationExpressions, fieldName)
  }

  private def addRuleExpressionToList(ruleExpression: (B) => Boolean): SimpleValidationRuleBuilder[A, B] = {
    val ruleList = currentRuleStructure match {
      case null => validationExpressions
      case x => validationExpressions :+ x
    }
    buildNextInstanceInChain(propertyExpression, SimpleValidationRuleStructureContainer[A, B](ruleExpression, None, None), ruleList, fieldName)
  }

  private lazy val notFunctor: (B => Boolean) => (B => Boolean) = originalExpression => parameter => !originalExpression(parameter)

  private def applyNotFunctor(expression: B => Boolean) = {
    notFunctor(expression)
  }

  protected[validation] def buildNextInstanceInChain(propertyExpression: A => B,
                                                 currentRuleStructure: SimpleValidationRuleStructureContainer[A, B],
                                                 validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                                 fieldName: String): SimpleValidationRuleBuilder[A, B] = {
    new SimpleValidationRuleBuilder(propertyExpression, currentRuleStructure, validationExpressions, fieldName)
  }

  protected[validation] override def buildRules: List[IValidationRule[A]] = {
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

