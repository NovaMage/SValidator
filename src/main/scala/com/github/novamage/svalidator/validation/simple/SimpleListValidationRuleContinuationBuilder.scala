package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.IRuleBuilder


class SimpleListValidationRuleContinuationBuilder[A, B](propertyListExpression: A => List[B],
                                                        currentRuleStructure: Option[SimpleValidationRuleStructureContainer[A, B]],
                                                        validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                                        fieldName: String,
                                                        markIndexesOfFieldNameErrors: Boolean,
                                                        previousMappedBuilderInChain: Option[IRuleBuilder[A]])
  extends SimpleListValidationRuleStarterBuilder[A, B](propertyListExpression,
    currentRuleStructure,
    validationExpressions,
    fieldName,
    markIndexesOfFieldNameErrors,
    previousMappedBuilderInChain) with IRuleBuilder[A] {

  def map[C](f: B => C): SimpleListValidationRuleStarterBuilder[A, C] = {
    new SimpleListValidationRuleStarterBuilder[A, C](
      propertyListExpression.andThen(_.map(f)),
      None,
      Nil,
      fieldName,
      markIndexesOfFieldNameErrors,
      Some(this)
    )
  }


  private lazy val defaultErrorMessageBuilder: ((A, B) => String) = (_, fieldValue) => s"$fieldValue is not a valid value for $fieldName"
  private lazy val defaultConditionedValidation: A => Boolean = _ => true

  def when(conditionedValidation: A => Boolean): SimpleListValidationRuleContinuationBuilder[A, B] = {
    buildNextInstanceInChain(propertyListExpression, currentRuleStructure.map(_.copy(conditionalValidation = Some(conditionedValidation))), validationExpressions, fieldName)
  }

  def withMessage(aFormatStringReceivingFieldValue: String): SimpleListValidationRuleContinuationBuilder[A, B] = {
    val errorMessageAlternateBuilder: ((A, B) => String) = (_, fieldValue) => aFormatStringReceivingFieldValue.format(fieldValue)
    buildNextInstanceInChain(propertyListExpression, currentRuleStructure.map(_.copy(errorMessageBuilder = Some(errorMessageAlternateBuilder))), validationExpressions, fieldName)
  }

  def withMetadata(key: String, value: Any): SimpleListValidationRuleContinuationBuilder[A, B] = {
    val nextCurrentRuleStructure = currentRuleStructure.map { ruleStructure =>
      val nextValueForKey = value :: ruleStructure.metadata.getOrElse(key, Nil)
      val nextMetadata = ruleStructure.metadata.updated(key, nextValueForKey)
      ruleStructure.copy(metadata = nextMetadata)
    }
    buildNextInstanceInChain(propertyListExpression, nextCurrentRuleStructure, validationExpressions, fieldName)
  }

  protected[validation] def buildRules(instance: A): RuleStreamCollection[A] = {
    val ruleStructures = currentRuleStructure match {
      case None => validationExpressions
      case Some(x) => validationExpressions :+ x
    }
    processRuleStructures(instance, ruleStructures)
  }


  private def processRuleStructures(instance: A,
                                    ruleStructuresList: List[SimpleValidationRuleStructureContainer[A, B]]): RuleStreamCollection[A] = {
    val upstream = previousMappedBuilderInChain.map(_.buildRules(instance))
    lazy val lazyPropertyListValue = propertyListExpression(instance)
    val mainRuleStream = ruleStructuresList.toStream map {
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
    RuleStreamCollection(List(ChainedValidationStream(List(mainRuleStream), upstream)))
  }


}
