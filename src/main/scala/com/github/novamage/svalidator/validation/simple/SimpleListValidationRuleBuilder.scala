package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.IRuleBuilder


class SimpleListValidationRuleBuilder[A, B](propertyListExpression: A => List[B],
                                            currentRuleStructure: SimpleValidationRuleStructureContainer[A, B],
                                            validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                            fieldName: String,
                                            markIndexesOfFieldNameErrors: Boolean) extends IRuleBuilder[A] {

  protected[validation] def buildNextInstanceInChain(propertyExpression: A => List[B],
                                                     currentRuleStructure: SimpleValidationRuleStructureContainer[A, B],
                                                     validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                                     fieldName: String): SimpleListValidationRuleBuilder[A,B] = {
    new SimpleListValidationRuleBuilder(propertyListExpression, currentRuleStructure, validationExpressions, fieldName, markIndexesOfFieldNameErrors)
  }

  private lazy val defaultErrorMessageBuilder: ((String, B) => String) = (fieldName, fieldValue) => s"$fieldValue is not a valid value for $fieldName"
  private lazy val defaultConditionedValidation: A => Boolean = x => true

  def when(conditionedValidation: A => Boolean) = {
    buildNextInstanceInChain(propertyListExpression, currentRuleStructure.copy(conditionalValidation = Some(conditionedValidation)), validationExpressions, fieldName)
  }

  def must(ruleExpressionReceivingPropertyValue: B => Boolean): SimpleListValidationRuleBuilder[A,B] = {
    val syntheticExpressionWithInstance: (B, A) => Boolean = (property, instance) => ruleExpressionReceivingPropertyValue(property)
    addRuleExpressionToList(syntheticExpressionWithInstance)
  }

  def mustComply(ruleExpressionReceivingPropertyAndInstanceValue: (B, A) => Boolean): SimpleListValidationRuleBuilder[A,B] = {
    addRuleExpressionToList(ruleExpressionReceivingPropertyAndInstanceValue)
  }


  def mustNot(ruleExpressionReceivingPropertyValue: B => Boolean): SimpleListValidationRuleBuilder[A,B] = {
    val syntheticExpressionWithInstance: (B, A) => Boolean = (property, instance) => ruleExpressionReceivingPropertyValue(property)
    addRuleExpressionToList(applyNotFunctor(syntheticExpressionWithInstance))
  }

  def mustNotComply(ruleExpressionReceivingPropertyValue: (B, A) => Boolean): SimpleListValidationRuleBuilder[A,B] = {
    addRuleExpressionToList(applyNotFunctor(ruleExpressionReceivingPropertyValue))
  }

  def withMessage(aFormatStringReceivingFieldNameAndValue: String): SimpleListValidationRuleBuilder[A,B] = {
    val errorMessageAlternateBuilder: ((String, B) => String) = (fieldName, fieldValue) => aFormatStringReceivingFieldNameAndValue.format(fieldName, fieldValue)
    buildNextInstanceInChain(propertyListExpression, currentRuleStructure.copy(errorMessageBuilder = Some(errorMessageAlternateBuilder)), validationExpressions, fieldName)
  }

  def withMessage(anExpressionReceivingFieldValue: B => String): SimpleListValidationRuleBuilder[A,B] = {
    val errorMessageAlternateBuilder: ((String, B) => String) = (fieldName, value) => anExpressionReceivingFieldValue(value)
    buildNextInstanceInChain(propertyListExpression, currentRuleStructure.copy(errorMessageBuilder = Some(errorMessageAlternateBuilder)), validationExpressions, fieldName)
  }

  def withMessage(expressionReceivingFieldNameAndValue: (String, B) => String): SimpleListValidationRuleBuilder[A,B] = {
    buildNextInstanceInChain(propertyListExpression, currentRuleStructure.copy(errorMessageBuilder = Some(expressionReceivingFieldNameAndValue)), validationExpressions, fieldName)
  }

  private def addRuleExpressionToList(ruleExpression: (B, A) => Boolean): SimpleListValidationRuleBuilder[A,B] = {
    val ruleList = currentRuleStructure match {
      case null => validationExpressions
      case x => validationExpressions :+ x
    }
    buildNextInstanceInChain(propertyListExpression, SimpleValidationRuleStructureContainer[A, B](ruleExpression, None, None), ruleList, fieldName)
  }

  private lazy val notFunctor: ((B, A) => Boolean) => ((B, A) => Boolean) = originalExpression => (propertyValue, instanceValue) => !originalExpression(propertyValue, instanceValue)

  private def applyNotFunctor(expression: (B, A) => Boolean) = {
    notFunctor(expression)
  }

  protected[validation] def buildRules(instance: A): RuleStreamCollection[A] = {
    val ruleStructures = currentRuleStructure match {
      case null => validationExpressions
      case x => validationExpressions :+ x
    }
    val currentStream = processRuleStructures(instance, ruleStructures)
    RuleStreamCollection(currentStream.ruleStreams)
  }


  private def processRuleStructures(instance: A, ruleStructuresList: List[SimpleValidationRuleStructureContainer[A, B]]): RuleStreamCollection[A] = {
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
