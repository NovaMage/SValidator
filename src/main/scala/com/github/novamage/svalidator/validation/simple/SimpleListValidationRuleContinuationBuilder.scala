package com.github.novamage.svalidator.validation.simple


class SimpleListValidationRuleContinuationBuilder[A, B, +C](propertyListExpression: A => List[B],
                                                            currentRuleStructure: Option[SimpleValidationRuleStructureContainer[A, B]],
                                                            validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                                            fieldName: String,
                                                            markIndexesOfFieldNameErrors: Boolean,
                                                            previousMappedBuilderInChain: Option[IRuleBuilder[A]],
                                                            previousMappedBuilderValueProvider: Option[UpstreamLazyValueProvider[List[C]]],
                                                            previousMappedBuilderValueConverter: Option[C => B])
  extends SimpleListValidationRuleStarterBuilder[A, B, C](propertyListExpression,
    currentRuleStructure,
    validationExpressions,
    fieldName,
    markIndexesOfFieldNameErrors,
    previousMappedBuilderInChain,
    previousMappedBuilderValueProvider,
    previousMappedBuilderValueConverter) with IRuleBuilder[A] with UpstreamLazyValueProvider[List[B]] {

  private var lazyExtractedProperty: LazyValueContainer[List[B]] = _

  override def fetchValue: List[B] = lazyExtractedProperty.extractValue

  def map[D](f: B => D): SimpleListValidationRuleStarterBuilder[A, D, B] = {
    new SimpleListValidationRuleStarterBuilder[A, D, B](
      propertyListExpression.andThen(_.map(f)),
      None,
      Nil,
      fieldName,
      markIndexesOfFieldNameErrors,
      Some(this),
      Some(this),
      Some(f)
    )
  }


  private lazy val defaultErrorMessageBuilder: ((A, B) => String) = (_, fieldValue) => s"$fieldValue is not a valid value for $fieldName"
  private lazy val defaultConditionedValidation: A => Boolean = _ => true

  def when(conditionedValidation: A => Boolean): SimpleListValidationRuleContinuationBuilder[A, B, C] = {
    buildNextInstanceInChain(propertyListExpression, currentRuleStructure.map(_.copy(conditionalValidation = Some(conditionedValidation))), validationExpressions, fieldName)
  }

  def withMessage(aFormatStringReceivingFieldValue: String): SimpleListValidationRuleContinuationBuilder[A, B, C] = {
    val errorMessageAlternateBuilder: ((A, B) => String) = (_, fieldValue) => aFormatStringReceivingFieldValue.format(fieldValue)
    buildNextInstanceInChain(propertyListExpression, currentRuleStructure.map(_.copy(errorMessageBuilder = Some(errorMessageAlternateBuilder))), validationExpressions, fieldName)
  }

  def withMetadata(key: String, value: Any): SimpleListValidationRuleContinuationBuilder[A, B, C] = {
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
    lazy val lazyPropertyListValue = (previousMappedBuilderValueProvider, previousMappedBuilderValueConverter) match {
      case (Some(provider), Some(converter)) => provider.fetchValue.map(converter)
      case _ => propertyListExpression(instance)
    }

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
    lazyExtractedProperty = new LazyValueContainer(lazyPropertyListValue)
    RuleStreamCollection(List(ChainedValidationStream(List(mainRuleStream), upstream)))
  }


}
