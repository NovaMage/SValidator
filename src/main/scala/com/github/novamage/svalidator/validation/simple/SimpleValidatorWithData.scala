package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation._
import com.github.novamage.svalidator.validation.simple.internals.{RuleBuilder, RuleStreamCollection}

/** Base class to provide fluent validation syntax
  *
  * @tparam A Type of objects to be validated by this class
  * @tparam B Type of data returned by summaries of this class
  */
abstract class SimpleValidatorWithData[A, B] extends Validator[A, B] {

  class ValidationDataContinuationBuilder(data: B) {

    /** Returns a [[com.github.novamage.svalidator.validation.ValidationWithData ValidationWithData]] by applying the given
      * [[com.github.novamage.svalidator.validation.simple.internals.RuleBuilder RuleBuilder]]s to the implicit instance.
      *
      * @see [[com.github.novamage.svalidator.validation.simple.SimpleValidatorWithData#For For]],
      *      [[com.github.novamage.svalidator.validation.simple.SimpleValidatorWithData#ForOptional ForOptional]],
      *      [[com.github.novamage.svalidator.validation.simple.SimpleValidatorWithData#ForEach ForEach]],
      *      [[com.github.novamage.svalidator.validation.simple.SimpleValidatorWithData#ForComponent ForComponent]],
      *      [[com.github.novamage.svalidator.validation.simple.SimpleValidatorWithData#ForOptionalComponent ForOptionalComponent]],
      *      [[com.github.novamage.svalidator.validation.simple.SimpleValidatorWithData#ForEachComponent ForEachComponent]]
      * @param ruleBuilders Builders to be applied to validate the instance
      * @param instance     Object to validate
      */
    def WithRules(ruleBuilders: RuleBuilder[A]*)(implicit instance: A): ValidationWithData[B] = {
      SimpleValidatorWithData.this.WithRulesAndData(Some(data), ruleBuilders: _*)
    }

  }

  /**
    * Starts building a validation chain that will include the given data in its result
    * @param data The data to attach to the returned [[com.github.novamage.svalidator.validation.ValidationWithData ValidationWithData]]
    */
  def WithData(data: B): ValidationDataContinuationBuilder = new ValidationDataContinuationBuilder(data)

  private def WithRulesAndData(data: Option[B], ruleBuilders: RuleBuilder[A]*)(implicit instance: A): ValidationWithData[B] = {
    val ruleStreamCollections = ruleBuilders.toList.map(_.buildRules(instance))
    val results = ruleStreamCollections.flatMap {
      collection => processRuleStreamCollection(instance, collection)
    }
    ValidationWithData(results, data)
  }

  /** Returns a [[com.github.novamage.svalidator.validation.ValidationWithData ValidationWithData]] by applying the given
    * [[com.github.novamage.svalidator.validation.simple.internals.RuleBuilder RuleBuilder]]s to the implicit instance.
    *
    * @see [[com.github.novamage.svalidator.validation.simple.SimpleValidatorWithData#For For]],
    *      [[com.github.novamage.svalidator.validation.simple.SimpleValidatorWithData#ForOptional ForOptional]],
    *      [[com.github.novamage.svalidator.validation.simple.SimpleValidatorWithData#ForEach ForEach]],
    *      [[com.github.novamage.svalidator.validation.simple.SimpleValidatorWithData#ForComponent ForComponent]],
    *      [[com.github.novamage.svalidator.validation.simple.SimpleValidatorWithData#ForOptionalComponent ForOptionalComponent]],
    *      [[com.github.novamage.svalidator.validation.simple.SimpleValidatorWithData#ForEachComponent ForEachComponent]]
    * @param ruleBuilders Builders to be applied to validate the instance
    * @param instance     Object to validate
    */
  def WithRules(ruleBuilders: RuleBuilder[A]*)(implicit instance: A): ValidationWithData[B] = {
    WithRulesAndData(None, ruleBuilders: _*)
  }


  /** Generates a [[com.github.novamage.svalidator.validation.simple.internals.RuleBuilder RuleBuilder]] that will only apply if
    * the given condition is true.
    *
    * @param conditionalExpression Condition to test
    * @return A continuation builder that will receive the [[RuleBuilder RuleBuilder]]s
    *         to apply if the condition is true.
    */
  def When(conditionalExpression: A => Boolean): ConditionedGroupValidationRuleBuilder[A] = {
    new ConditionedGroupValidationRuleBuilder(conditionalExpression)
  }

  /** Starts a chain to generate a [[com.github.novamage.svalidator.validation.simple.internals.RuleBuilder RuleBuilder]] for the
    * specified property expression.
    *
    * @param propertyExpression Extractor for the property to be validated
    * @return The continuation builder that will require the property name
    */
  def For[C](propertyExpression: A => C): FieldListRequiringSimpleValidatorRuleBuilder[A, C] = {
    val composedFunction: A => List[C] = x => propertyExpression(x) :: Nil
    new FieldListRequiringSimpleValidatorRuleBuilder[A, C](composedFunction, false)
  }

  /** Starts a chain to generate a [[com.github.novamage.svalidator.validation.simple.internals.RuleBuilder RuleBuilder]] for the
    * specified list property.
    *
    * Any validations will be applied to each element and errors will be indexed with square brackets with the property name (
    * e.g.: myProperty[3])
    *
    * @param propertyListExpression Extractor for the property list to be validated
    * @return The continuation builder that will require the property name
    */
  def ForEach[C](propertyListExpression: A => List[C]): FieldListRequiringSimpleValidatorRuleBuilder[A, C] = {
    new FieldListRequiringSimpleValidatorRuleBuilder[A, C](propertyListExpression, true)
  }

  /** Starts a chain to generate a [[com.github.novamage.svalidator.validation.simple.internals.RuleBuilder RuleBuilder]] for the
    * specified optional property.
    *
    * Any validations will only be applied to if the option is defined.
    *
    * @param optionalPropertyExpression Extractor for the optional property to be validated
    * @return The continuation builder that will require the property name
    */
  def ForOptional[C](optionalPropertyExpression: A => Option[C]): FieldListRequiringSimpleValidatorRuleBuilder[A, C] = {
    val composedFunction: A => List[C] = x => optionalPropertyExpression(x).toList
    new FieldListRequiringSimpleValidatorRuleBuilder[A, C](composedFunction, false)
  }

  /** Starts a chain to delegate validation of a component to an external [[com.github.novamage.svalidator.validation.Validator Validator]]
    *
    * The results of the delegated validator will have the property name plus a dot prepended to their field names, and merged with
    * the results of the current validator (e.g.: myComponent.someFieldName).
    *
    * @param componentPropertyExpression Extractor for the component property to be validated
    * @return The continuation builder that will require the property name
    */
  def ForComponent[C](componentPropertyExpression: A => C): ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, C] = {
    val composedFunction: A => List[C] = x => componentPropertyExpression(x) :: Nil
    new ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, C](composedFunction, false)
  }

  /** Starts a chain to delegate validation of a list of components to an external [[com.github.novamage.svalidator.validation.Validator Validator]]
    *
    * The results of the delegated validator will have the property name of the component indexed by the position of the
    * validated component with square brackets followed by the field name(e.g.: myComponent[3].someFieldName) and merged
    * with the results of the current validator .
    *
    * @param componentListPropertyExpression Extractor for the component property list to be validated
    * @return The continuation builder that will require the component property name
    */
  def ForEachComponent[C](componentListPropertyExpression: A => List[C]): ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, C] = {
    new ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, C](componentListPropertyExpression, true)
  }

  /** Starts a chain to delegate validation of an optional component to an external [[com.github.novamage.svalidator.validation.Validator Validator]]
    *
    * The results of the delegated validator will have the property name plus a dot prepended to their field names, and merged with
    * the results of the current validator (e.g.: myComponent.someFieldName). Validations will only be applied if the
    * component is present.
    *
    * @param optionalComponentPropertyExpression Extractor for the optional component to be validated
    * @return The continuation builder that will require the component property name
    */
  def ForOptionalComponent[C](optionalComponentPropertyExpression: A => Option[C]): ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, C] = {
    val composedFunction: A => List[C] = x => optionalComponentPropertyExpression(x).toList
    new ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, C](composedFunction, false)
  }

  private def processRuleStreamCollection(instance: A,
                                          collection: RuleStreamCollection[A]): List[ValidationFailure] = {
    collection.chains.flatMap { chain =>
      val upstreamResults = chain.dependsOnUpstream.map(processRuleStreamCollection(instance, _)).getOrElse(Nil)
      if (upstreamResults.isEmpty) {
        chain.mainStream.flatMap {
          ruleStream =>
            ruleStream map { _.apply(instance) } collectFirst { case result if result.nonEmpty => result }
        }.flatten
      } else {
        upstreamResults
      }
    }
  }
}

