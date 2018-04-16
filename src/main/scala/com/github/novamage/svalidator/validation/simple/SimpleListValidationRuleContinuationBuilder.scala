package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.IRuleBuilder


class SimpleListValidationRuleContinuationBuilder[A, B](propertyListExpression: A => List[B],
                                                        currentRuleStructure: SimpleValidationRuleStructureContainer[A, B],
                                                        validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                                        fieldName: String,
                                                        markIndexesOfFieldNameErrors: Boolean)
  extends SimpleListValidationRuleStarterBuilder[A, B](propertyListExpression,
    currentRuleStructure,
    validationExpressions,
    fieldName,
    markIndexesOfFieldNameErrors) with IRuleBuilder[A] {

  private lazy val defaultErrorMessageBuilder: ((A, B) => String) = (_, fieldValue) => s"$fieldValue is not a valid value for $fieldName"
  private lazy val defaultConditionedValidation: A => Boolean = _ => true

  def when(conditionedValidation: A => Boolean): SimpleListValidationRuleContinuationBuilder[A, B] = {
    buildNextInstanceInChain(propertyListExpression, currentRuleStructure.copy(conditionalValidation = Some(conditionedValidation)), validationExpressions, fieldName)
  }

  def withMessage(aFormatStringReceivingFieldValue: String): SimpleListValidationRuleContinuationBuilder[A, B] = {
    val errorMessageAlternateBuilder: ((A, B) => String) = (_, fieldValue) => aFormatStringReceivingFieldValue.format(fieldValue)
    buildNextInstanceInChain(propertyListExpression, currentRuleStructure.copy(errorMessageBuilder = Some(errorMessageAlternateBuilder)), validationExpressions, fieldName)
  }

  def withMetadata(key: String, value: Any): SimpleListValidationRuleContinuationBuilder[A, B] = {
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
