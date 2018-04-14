package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.IRuleBuilder


class SimpleListValidationRuleBuilder[A, B](propertyListExpression: A => List[B],
                                            currentRuleStructure: SimpleValidationRuleStructureContainer[A, B],
                                            validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                            fieldName: String,
                                            markIndexesOfFieldNameErrors: Boolean) extends IRuleBuilder[A] {

  private lazy val defaultErrorMessageBuilder: ((A, B) => String) = (_, fieldValue) => s"$fieldValue is not a valid value for $fieldName"
  private lazy val defaultConditionedValidation: A => Boolean = _ => true
  private lazy val notFunctor: ((B, A) => Boolean) => ((B, A) => Boolean) = originalExpression => (propertyValue, instanceValue) => !originalExpression(propertyValue, instanceValue)

  def when(conditionedValidation: A => Boolean): SimpleListValidationRuleBuilder[A, B] = {
    buildNextInstanceInChain(propertyListExpression, currentRuleStructure.copy(conditionalValidation = Some(conditionedValidation)), validationExpressions, fieldName)
  }

  def must(ruleExpressionReceivingPropertyValue: B => Boolean): SimpleListValidationRuleBuilder[A, B] = {
    val syntheticExpressionWithInstance: (B, A) => Boolean = (property, _) => ruleExpressionReceivingPropertyValue(property)
    addRuleExpressionToList(syntheticExpressionWithInstance)
  }

  private def addRuleExpressionToList(ruleExpression: (B, A) => Boolean): SimpleListValidationRuleBuilder[A, B] = {
    val ruleList = currentRuleStructure match {
      case null => validationExpressions
      case x => validationExpressions :+ x
    }
    buildNextInstanceInChain(propertyListExpression, SimpleValidationRuleStructureContainer[A, B](ruleExpression, None, None, Map.empty), ruleList, fieldName)
  }

  protected[validation] def buildNextInstanceInChain(propertyExpression: A => List[B],
                                                     currentRuleStructure: SimpleValidationRuleStructureContainer[A, B],
                                                     validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                                     fieldName: String): SimpleListValidationRuleBuilder[A, B] = {
    new SimpleListValidationRuleBuilder(propertyListExpression, currentRuleStructure, validationExpressions, fieldName, markIndexesOfFieldNameErrors)
  }

  def mustNot(ruleExpressionReceivingPropertyValue: B => Boolean): SimpleListValidationRuleBuilder[A, B] = {
    val syntheticExpressionWithInstance: (B, A) => Boolean = (property, _) => ruleExpressionReceivingPropertyValue(property)
    addRuleExpressionToList(applyNotFunctor(syntheticExpressionWithInstance))
  }

  private def applyNotFunctor(expression: (B, A) => Boolean): (B, A) => Boolean = {
    notFunctor(expression)
  }

  def withMessage(aFormatStringReceivingFieldValue: String): SimpleListValidationRuleBuilder[A, B] = {
    val errorMessageAlternateBuilder: ((A, B) => String) = (_, fieldValue) => aFormatStringReceivingFieldValue.format(fieldValue)
    buildNextInstanceInChain(propertyListExpression, currentRuleStructure.copy(errorMessageBuilder = Some(errorMessageAlternateBuilder)), validationExpressions, fieldName)
  }

  def withMetadata(key: String, value: Any): SimpleListValidationRuleBuilder[A, B] = {
    val currentMetadata = currentRuleStructure.metadata
    val nextValueForKey = value :: currentMetadata.getOrElse(key, Nil)
    val nextMetadata = currentMetadata.updated(key, nextValueForKey)
    buildNextInstanceInChain(propertyListExpression, currentRuleStructure.copy(metadata = nextMetadata), validationExpressions, fieldName)
  }

  protected[validation] def buildRules(instance: A): RuleStreamCollection[A] = {
    val ruleStructures = currentRuleStructure match {
      case null => validationExpressions
      case x => validationExpressions :+ x
    }
    processRuleStructures(instance, ruleStructures)
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
          markIndexesOfFieldNameErrors,
          ruleStructureContainer.metadata)
    }
    RuleStreamCollection(List(ruleStream))
  }

}
