package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation._
import com.github.novamage.svalidator.validation.simple.internals.{RuleBuilder, RuleStreamCollection}

/** Base class to provide fluent validation syntax
  *
  * @tparam A Type of objects to be validated by this class
  */
abstract class SimpleValidator[A] extends Validator[A] {

  /** Returns a [[com.github.novamage.svalidator.validation.ValidationSummary ValidationSummary]] by applying the given
    * [[com.github.novamage.svalidator.validation.simple.internals.RuleBuilder RuleBuilder]]s to the implicit instance.
    *
    * @see [[com.github.novamage.svalidator.validation.simple.SimpleValidator#For For]],
    *     [[com.github.novamage.svalidator.validation.simple.SimpleValidator#ForOptional ForOptional]],
    *     [[com.github.novamage.svalidator.validation.simple.SimpleValidator#ForEach ForEach]],
    *     [[com.github.novamage.svalidator.validation.simple.SimpleValidator#ForComponent ForComponent]],
    *     [[com.github.novamage.svalidator.validation.simple.SimpleValidator#ForOptionalComponent ForOptionalComponent]],
    *     [[com.github.novamage.svalidator.validation.simple.SimpleValidator#ForEachComponent ForEachComponent]]
    * @param ruleBuilders Builders to be applied to validate the instance
    * @param instance Object to validate
    */
  def WithRules(ruleBuilders: RuleBuilder[A]*)(implicit instance: A): ValidationSummary = {
    val ruleStreamCollections = ruleBuilders.toList.map(_.buildRules(instance))
    val results = ruleStreamCollections.flatMap {
      collection => processRuleStreamCollection(instance, collection)
    }
    ValidationSummary(results)
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
  def For[B](propertyExpression: A => B): FieldListRequiringSimpleValidatorRuleBuilder[A, B] = {
    val composedFunction: A => List[B] = x => propertyExpression(x) :: Nil
    new FieldListRequiringSimpleValidatorRuleBuilder[A, B](composedFunction, false)
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
  def ForEach[B](propertyListExpression: A => List[B]): FieldListRequiringSimpleValidatorRuleBuilder[A, B] = {
    new FieldListRequiringSimpleValidatorRuleBuilder[A, B](propertyListExpression, true)
  }

  /** Starts a chain to generate a [[com.github.novamage.svalidator.validation.simple.internals.RuleBuilder RuleBuilder]] for the
    * specified optional property.
    *
    * Any validations will only be applied to if the option is defined.
    *
    * @param optionalPropertyExpression Extractor for the optional property to be validated
    * @return The continuation builder that will require the property name
    */
  def ForOptional[B](optionalPropertyExpression: A => Option[B]): FieldListRequiringSimpleValidatorRuleBuilder[A, B] = {
    val composedFunction: A => List[B] = x => optionalPropertyExpression(x).toList
    new FieldListRequiringSimpleValidatorRuleBuilder[A, B](composedFunction, false)
  }

  /** Starts a chain to delegate validation of a component to an external [[com.github.novamage.svalidator.validation.Validator Validator]]
    *
    * The results of the delegated validator will have the property name plus a dot prepended to their field names, and merged with
    * the results of the current validator (e.g.: myComponent.someFieldName).
    *
    * @param componentPropertyExpression Extractor for the component property to be validated
    * @return The continuation builder that will require the property name
    */
  def ForComponent[B](componentPropertyExpression: A => B): ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, B] = {
    val composedFunction: A => List[B] = x => componentPropertyExpression(x) :: Nil
    new ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, B](composedFunction, false)
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
  def ForEachComponent[B](componentListPropertyExpression: A => List[B]): ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, B] = {
    new ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, B](componentListPropertyExpression, true)
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
  def ForOptionalComponent[B](optionalComponentPropertyExpression: A => Option[B]): ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, B] = {
    val composedFunction: A => List[B] = x => optionalComponentPropertyExpression(x).toList
    new ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, B](composedFunction, false)
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

