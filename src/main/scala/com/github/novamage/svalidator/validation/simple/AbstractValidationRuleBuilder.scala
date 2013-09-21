package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.{IValidationRule, IRuleBuilder}

//A is the type of the whole validated instance
//B is the type of the properly directly extracted
//C is they direct type used on validation rules (varies according if it's a list, an option, etc)

abstract class AbstractValidationRuleBuilder[A, B, C](propertyExpression: A => B,
                                                      currentRuleStructure: SimpleValidationRuleStructureContainer[A, C],
                                                      validationExpressions: List[SimpleValidationRuleStructureContainer[A, C]],
                                                      fieldName: String,
                                                      previousMappedBuilder: Option[AbstractValidationRuleBuilder[A, _, _]] = None) extends IRuleBuilder[A] {


  lazy val defaultErrorMessageBuilder: ((String, C) => String) = (fieldName, fieldValue) => s"$fieldValue is not a valid value for $fieldName"
  lazy val defaultConditionedValidation: A => Boolean = x => true

  protected[validation] def buildNextInstanceInChain(propertyExpression: A => B,
                                                     currentRuleStructure: SimpleValidationRuleStructureContainer[A, C],
                                                     validationExpressions: List[SimpleValidationRuleStructureContainer[A, C]],
                                                     fieldName: String,
                                                     previousMappedBuilder: Option[AbstractValidationRuleBuilder[A, _, _]]): AbstractValidationRuleBuilder[A, B, C]

  protected[validation] def processRuleStructures(instance: A, ruleStructuresList: List[SimpleValidationRuleStructureContainer[A, C]]): Stream[IValidationRule[A]]

  final protected[validation] def buildRules(instance: A): Stream[IValidationRule[A]] = {
    val ruleStructures = currentRuleStructure match {
      case null => validationExpressions
      case x => validationExpressions :+ x
    }
    previousMappedBuilder.map(_.buildRules(instance)).getOrElse(Stream.Empty) ++ processRuleStructures(instance, ruleStructures)
  }

  final def when(conditionedValidation: A => Boolean) = {
    buildNextInstanceInChain(propertyExpression, currentRuleStructure.copy(conditionalValidation = Some(conditionedValidation)), validationExpressions, fieldName, previousMappedBuilder)
  }

  def must(ruleExpression: C => Boolean): AbstractValidationRuleBuilder[A, B, C] = {
    val syntheticExpressionWithInstance: (C, A) => Boolean = (property, instance) => ruleExpression(property)
    addRuleExpressionToList(syntheticExpressionWithInstance)
  }

  def must(ruleExpression: (C, A) => Boolean): AbstractValidationRuleBuilder[A, B, C] = {
    addRuleExpressionToList(ruleExpression)
  }


  def mustNot(ruleExpression: C => Boolean): AbstractValidationRuleBuilder[A, B, C] = {
    val syntheticExpressionWithInstance: (C, A) => Boolean = (property, instance) => ruleExpression(property)
    addRuleExpressionToList(applyNotFunctor(syntheticExpressionWithInstance))
  }

  def mustNot(ruleExpression: (C, A) => Boolean): AbstractValidationRuleBuilder[A, B, C] = {
    addRuleExpressionToList(applyNotFunctor(ruleExpression))
  }

  final def withMessage(aFormatStringReceivingFieldNameAndValue: String): AbstractValidationRuleBuilder[A, B, C] = {
    val errorMessageAlternateBuilder: ((String, C) => String) = (fieldName, fieldValue) => aFormatStringReceivingFieldNameAndValue.format(fieldName, fieldValue)
    buildNextInstanceInChain(propertyExpression, currentRuleStructure.copy(errorMessageBuilder = Some(errorMessageAlternateBuilder)), validationExpressions, fieldName, previousMappedBuilder)
  }

  final def withMessage(anExpressionReceivingFieldValue: C => String): AbstractValidationRuleBuilder[A, B, C] = {
    val errorMessageAlternateBuilder: ((String, C) => String) = (fieldName, value) => anExpressionReceivingFieldValue(value)
    buildNextInstanceInChain(propertyExpression, currentRuleStructure.copy(errorMessageBuilder = Some(errorMessageAlternateBuilder)), validationExpressions, fieldName, previousMappedBuilder)
  }

  final def withMessage(expressionReceivingFieldNameAndValue: (String, C) => String): AbstractValidationRuleBuilder[A, B, C] = {
    buildNextInstanceInChain(propertyExpression, currentRuleStructure.copy(errorMessageBuilder = Some(expressionReceivingFieldNameAndValue)), validationExpressions, fieldName, previousMappedBuilder)
  }

  private def addRuleExpressionToList(ruleExpression: (C, A) => Boolean): AbstractValidationRuleBuilder[A, B, C] = {
    val ruleList = currentRuleStructure match {
      case null => validationExpressions
      case x => validationExpressions :+ x
    }
    buildNextInstanceInChain(propertyExpression, SimpleValidationRuleStructureContainer[A, C](ruleExpression, None, None), ruleList, fieldName, previousMappedBuilder)
  }

  private lazy val notFunctor: ((C, A) => Boolean) => ((C, A) => Boolean) = originalExpression => (propertyValue, instanceValue) => !originalExpression(propertyValue, instanceValue)

  private def applyNotFunctor(expression: (C, A) => Boolean) = {
    notFunctor(expression)
  }


}