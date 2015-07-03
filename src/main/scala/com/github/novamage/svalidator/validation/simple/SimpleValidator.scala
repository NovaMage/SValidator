package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation._

abstract class SimpleValidator[A] extends IValidate[A] {

  def buildRules(input: A): List[IRuleBuilder[A]]

  override def validate(input: A): ValidationSummary = {
    val ruleStreamCollections: List[RuleStreamCollection[A]] = buildRules(input).map(_.buildRules(input))
    val nonFlattenedValidationRuleStreams = ruleStreamCollections.flatMap(_.ruleStreams)
    val firstFailingResultForEachGroup: List[List[ValidationFailure]] =
      nonFlattenedValidationRuleStreams flatMap {
        ruleStream =>
          ruleStream map { _.apply(input) } collectFirst { case result if result.nonEmpty => result }
      }
    ValidationSummary(firstFailingResultForEachGroup.flatten)
  }

  def When(conditionalExpression: A => Boolean) = {
    new ConditionedGroupValidationRuleBuilder(conditionalExpression)
  }

  def For[B](propertyExpression: A => B) = {
    val composedFunction: (A => List[B]) = x => propertyExpression(x) :: Nil
    new FieldListRequiringSimpleValidatorRuleBuilder[A, B](composedFunction, false)
  }

  def ForEach[B](propertyListExpression: A => List[B]) = {
    new FieldListRequiringSimpleValidatorRuleBuilder[A, B](propertyListExpression, true)
  }

  def ForOptional[B](optionalPropertyExpression: A => Option[B]) = {
    val composedFunction: (A => List[B]) = x => optionalPropertyExpression(x).toList
    new FieldListRequiringSimpleValidatorRuleBuilder[A, B](composedFunction, false)
  }

  def ForComponent[B](componentPropertyExpression: A => B) = {
    val composedFunction: (A => List[B]) = x => componentPropertyExpression(x) :: Nil
    new ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, B](composedFunction, false)
  }

  def ForEachComponent[B](componentListPropertyExpression: A => List[B]) = {
    new ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, B](componentListPropertyExpression, true)
  }

  def ForOptionalComponent[B](optionalComponentPropertyExpression: A => Option[B]) = {
    val composedFunction: (A => List[B]) = x => optionalComponentPropertyExpression(x).toList
    new ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, B](composedFunction, false)
  }

}

