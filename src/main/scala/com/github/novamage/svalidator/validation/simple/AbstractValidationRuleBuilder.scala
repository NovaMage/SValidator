package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.IRuleBuilder

//A is the type of the whole validated instance
//B is the type of the properly directly extracted
//C is the direct type used on validation rules (varies according if it's a list, an option, etc)

abstract class AbstractValidationRuleBuilder[A, B, C](propertyExpression: A => B,
                                                      currentRuleStructure: SimpleValidationRuleStructureContainer[A, C],
                                                      validationExpressions: List[SimpleValidationRuleStructureContainer[A, C]],
                                                      fieldName: String) extends IRuleBuilder[A] {


  lazy val defaultErrorMessageBuilder: ((String, C) => String) = (fieldName, fieldValue) => s"$fieldValue is not a valid value for $fieldName"
  lazy val defaultConditionedValidation: A => Boolean = x => true

  protected[validation] def buildNextInstanceInChain(propertyExpression: A => B,
                                                     currentRuleStructure: SimpleValidationRuleStructureContainer[A, C],
                                                     validationExpressions: List[SimpleValidationRuleStructureContainer[A, C]],
                                                     fieldName: String): AbstractValidationRuleBuilder[A, B, C]

  protected[validation] def processRuleStructures(instance: A, ruleStructuresList: List[SimpleValidationRuleStructureContainer[A, C]]): RuleStreamCollection[A]

  final protected[validation] def buildRules(instance: A): RuleStreamCollection[A] = {
    val ruleStructures = currentRuleStructure match {
      case null => validationExpressions
      case x => validationExpressions :+ x
    }
    val currentStream = processRuleStructures(instance, ruleStructures)
    RuleStreamCollection(currentStream.ruleStreams)
  }

  final def when(conditionedValidation: A => Boolean) = {
    buildNextInstanceInChain(propertyExpression, currentRuleStructure.copy(conditionalValidation = Some(conditionedValidation)), validationExpressions, fieldName)
  }

  def must(ruleExpressionReceivingPropertyValue: C => Boolean): AbstractValidationRuleBuilder[A, B, C] = {
    val syntheticExpressionWithInstance: (C, A) => Boolean = (property, instance) => ruleExpressionReceivingPropertyValue(property)
    addRuleExpressionToList(syntheticExpressionWithInstance)
  }

  def mustComply(ruleExpressionReceivingPropertyAndInstanceValue: (C, A) => Boolean): AbstractValidationRuleBuilder[A, B, C] = {
    addRuleExpressionToList(ruleExpressionReceivingPropertyAndInstanceValue)
  }


  def mustNot(ruleExpressionReceivingPropertyValue: C => Boolean): AbstractValidationRuleBuilder[A, B, C] = {
    val syntheticExpressionWithInstance: (C, A) => Boolean = (property, instance) => ruleExpressionReceivingPropertyValue(property)
    addRuleExpressionToList(applyNotFunctor(syntheticExpressionWithInstance))
  }

  def mustNotComply(ruleExpressionReceivingPropertyValue: (C, A) => Boolean): AbstractValidationRuleBuilder[A, B, C] = {
    addRuleExpressionToList(applyNotFunctor(ruleExpressionReceivingPropertyValue))
  }

  final def withMessage(aFormatStringReceivingFieldNameAndValue: String): AbstractValidationRuleBuilder[A, B, C] = {
    val errorMessageAlternateBuilder: ((String, C) => String) = (fieldName, fieldValue) => aFormatStringReceivingFieldNameAndValue.format(fieldName, fieldValue)
    buildNextInstanceInChain(propertyExpression, currentRuleStructure.copy(errorMessageBuilder = Some(errorMessageAlternateBuilder)), validationExpressions, fieldName)
  }

  final def withMessage(anExpressionReceivingFieldValue: C => String): AbstractValidationRuleBuilder[A, B, C] = {
    val errorMessageAlternateBuilder: ((String, C) => String) = (fieldName, value) => anExpressionReceivingFieldValue(value)
    buildNextInstanceInChain(propertyExpression, currentRuleStructure.copy(errorMessageBuilder = Some(errorMessageAlternateBuilder)), validationExpressions, fieldName)
  }

  final def withMessage(expressionReceivingFieldNameAndValue: (String, C) => String): AbstractValidationRuleBuilder[A, B, C] = {
    buildNextInstanceInChain(propertyExpression, currentRuleStructure.copy(errorMessageBuilder = Some(expressionReceivingFieldNameAndValue)), validationExpressions, fieldName)
  }

  private def addRuleExpressionToList(ruleExpression: (C, A) => Boolean): AbstractValidationRuleBuilder[A, B, C] = {
    val ruleList = currentRuleStructure match {
      case null => validationExpressions
      case x => validationExpressions :+ x
    }
    buildNextInstanceInChain(propertyExpression, SimpleValidationRuleStructureContainer[A, C](ruleExpression, None, None), ruleList, fieldName)
  }

  private lazy val notFunctor: ((C, A) => Boolean) => ((C, A) => Boolean) = originalExpression => (propertyValue, instanceValue) => !originalExpression(propertyValue, instanceValue)

  private def applyNotFunctor(expression: (C, A) => Boolean) = {
    notFunctor(expression)
  }


}
