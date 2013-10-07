package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation._

abstract class SimpleValidator[A] extends IValidate[A] {

  def buildRules: List[IRuleBuilder[A]]

  override def validate(instance: A): ValidationSummary = {
    val ruleStreamCollections: List[RuleStreamCollection[A]] = buildRules.map(_.buildRules(instance))
    val unflattenedValidationRuleStreams = ruleStreamCollections.flatMap(_.ruleStreams)
    val firstFailingResultForEachGroup: List[List[ValidationFailure]] =
      unflattenedValidationRuleStreams map {
        ruleStream =>
          ruleStream map {_.apply(instance)} collectFirst {
            case result if !result.isEmpty => result
          }
      } collect {
        case Some(x) => x
      }
    new ValidationSummary(firstFailingResultForEachGroup.flatten)
  }

  def When(conditionalExpression: A => Boolean) = {
    new ConditionedGroupValidationRuleBuilder(conditionalExpression)
  }

  def For[B](propertyExpression: A => B) = {
    val composedFunction: (A => List[B]) = x => List(propertyExpression(x))
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
    val composedFunction: (A => List[B]) = x => List(componentPropertyExpression(x))
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

