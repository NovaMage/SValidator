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

  /** Converts the extracted property of the preceding chain by applying <strong>once</strong> the function <code>f</code> only if all the preceding
    * validations are successful.  Further calls down the chain will work the type of the converted value.
    *
    *
    * @param f Function to convert the extracted property
    * @tparam D The new type of the property chain
    */
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


  /** Causes the preceding [[com.github.novamage.svalidator.validation.simple.SimpleValidator#must must]] or
    * [[[[com.github.novamage.svalidator.validation.simple.SimpleValidator#mustNot mustNot]] call to be applied only if
    * the passed in condition evaluates to true
    *
    * @param conditionedValidation Condition to be applied to the instance
    */
  def when(conditionedValidation: A => Boolean): SimpleListValidationRuleContinuationBuilder[A, B, C] = {
    buildNextInstanceInChain(propertyListExpression, currentRuleStructure.map(_.copy(conditionalValidation = Some(conditionedValidation))), validationExpressions, fieldName)
  }

  /** Assigns the passed in as the error message for the preceding [[com.github.novamage.svalidator.validation.simple.SimpleValidator#must must]] or
    * [[[[com.github.novamage.svalidator.validation.simple.SimpleValidator#mustNot mustNot]] call.
    *
    * @param messageKey The raw message or a key string for localized messages
    * @param argsFunction A function that receives the value of the property (or a single element if using an `Each`
    *                     builder), and should return the list of arguments used to format the error message.
    */
  def withMessage(messageKey: String, argsFunction: B => List[Any]): SimpleListValidationRuleContinuationBuilder[A, B, C] = {
    buildNextInstanceInChain(
      propertyListExpression,
      currentRuleStructure.map(_.copy(errorMessageKey = Some(messageKey), errorMessageFormatValues = Some(argsFunction))),
      validationExpressions,
      fieldName)
  }

  /** Assigns the passed in as the error message for the preceding [[com.github.novamage.svalidator.validation.simple.SimpleValidator#must must]] or
    * [[[[com.github.novamage.svalidator.validation.simple.SimpleValidator#mustNot mustNot]] call.
    *
    * @param messageKey The raw message or a key string for localized messages
    * @param args List of values to use when formatting the message.
    */
  def withMessage(messageKey: String, args: Any*): SimpleListValidationRuleContinuationBuilder[A, B, C] = {
    val formatValues = Some((_: B) => args.toList).filter(_ => args.nonEmpty)
    buildNextInstanceInChain(
      propertyListExpression,
      currentRuleStructure.map(_.copy(errorMessageKey = Some(messageKey), errorMessageFormatValues = formatValues)),
      validationExpressions,
      fieldName)
  }

  /** Assigns the specified value to the specified key in the metadata for the preceding
    * [[com.github.novamage.svalidator.validation.simple.SimpleValidator#must must]] or
    * [[[[com.github.novamage.svalidator.validation.simple.SimpleValidator#mustNot mustNot]] call, if they generate a
    * validation failure.  If the key already exists, the value is appended to the list of values of said key.
    *
    * @param key Key to associate
    * @param value Value for the given key
    */
  def withMetadata(key: String, value: Any): SimpleListValidationRuleContinuationBuilder[A, B, C] = {
    val nextCurrentRuleStructure = currentRuleStructure.map { ruleStructure =>
      val nextValueForKey = value :: ruleStructure.metadata.getOrElse(key, Nil)
      val nextMetadata = ruleStructure.metadata.updated(key, nextValueForKey)
      ruleStructure.copy(metadata = nextMetadata)
    }
    buildNextInstanceInChain(propertyListExpression, nextCurrentRuleStructure, validationExpressions, fieldName)
  }

  override protected[validation] def buildRules(instance: A): RuleStreamCollection[A] = {
    val ruleStructures = currentRuleStructure match {
      case None => validationExpressions
      case Some(x) => validationExpressions :+ x
    }
    processRuleStructures(instance, ruleStructures)
  }

  override protected[simple] def fetchValue: List[B] = lazyExtractedProperty.extractValue

  private lazy val defaultConditionedValidation: A => Boolean = _ => true

  private var lazyExtractedProperty: LazyValueContainer[List[B]] = _

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
