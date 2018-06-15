package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.simple.internals._


/** Builder for appending messages, metadata, adding conditional validation, or mapping properties into something else.
  *
  * @tparam A Type of the instance being validated
  * @tparam B Type of the extracted property being validated
  * @tparam C If the property has been mapped, type the property had before the map, otherwise, [[scala.Nothing Nothing]]
  */
class SimpleListValidationRuleContinuationBuilder[A, B, +C](propertyListExpression: A => List[B],
                                                            currentRuleStructure: Option[SimpleValidationRuleStructureContainer[A, B]],
                                                            validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                                            fieldName: String,
                                                            markIndexesOfFieldNameErrors: Boolean,
                                                            previousMappedBuilderInChain: Option[RuleBuilder[A]],
                                                            previousMappedBuilderValueProvider: Option[UpstreamLazyValueProvider[List[C]]],
                                                            previousMappedBuilderValueConverter: Option[C => B])
  extends SimpleListValidationRuleStarterBuilder[A, B, C](propertyListExpression,
    currentRuleStructure,
    validationExpressions,
    fieldName,
    markIndexesOfFieldNameErrors,
    previousMappedBuilderInChain,
    previousMappedBuilderValueProvider,
    previousMappedBuilderValueConverter) with RuleBuilder[A] with UpstreamLazyValueProvider[List[B]] {

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


  private lazy val defaultConditionedValidation: A => Boolean = _ => true

  def when(conditionedValidation: A => Boolean): SimpleListValidationRuleContinuationBuilder[A, B, C] = {
    buildNextInstanceInChain(propertyListExpression, currentRuleStructure.map(_.copy(conditionalValidation = Some(conditionedValidation))), validationExpressions, fieldName)
  }

  def withMessage(messageKey: String, args: B => List[Any]): SimpleListValidationRuleContinuationBuilder[A, B, C] = {
    buildNextInstanceInChain(
      propertyListExpression,
      currentRuleStructure.map(_.copy(errorMessageKey = Some(messageKey), errorMessageFormatValues = Some(args))),
      validationExpressions,
      fieldName)
  }

  def withMessage(messageKey: String, args: Any*): SimpleListValidationRuleContinuationBuilder[A, B, C] = {
    val formatValues = Some((_: B) => args.toList).filter(_ => args.nonEmpty)
    buildNextInstanceInChain(
      propertyListExpression,
      currentRuleStructure.map(_.copy(errorMessageKey = Some(messageKey), errorMessageFormatValues = formatValues)),
      validationExpressions,
      fieldName)
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
          ruleStructureContainer.errorMessageKey,
          ruleStructureContainer.errorMessageFormatValues,
          ruleStructureContainer.conditionalValidation.getOrElse(defaultConditionedValidation),
          markIndexesOfFieldNameErrors,
          ruleStructureContainer.metadata)
    }
    lazyExtractedProperty = new LazyValueContainer(lazyPropertyListValue)
    RuleStreamCollection(List(ChainedValidationStream(List(mainRuleStream), upstream)))
  }


}
