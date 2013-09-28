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


  def For[B](propertyExpression: A => B) = {
    new FieldRequiringSimpleValidationRuleBuilder[A, B](propertyExpression)

  }

  def ForEach[B](propertyListExpression: A => List[B]) = {
    new FieldListRequiringSimpleValidatorRuleBuilder[A, B](propertyListExpression)
  }

  def ForOptional[B](optionalPropertyExpression: A => Option[B]) = {
    new OptionalFieldRequiringSimpleValidatorRuleBuilder[A, B](optionalPropertyExpression)
  }

  def ForComponent[B](componentPropertyExpression: A => B) = {
    new ComponentFieldRequiringSimpleValidatorRuleBuilder[A, B](componentPropertyExpression)
  }

  def When(conditionalExpression: A => Boolean) = {
    new ConditionedGroupValidationRuleBuilder(conditionalExpression)
  }

  def ForOptionalComponent[B](optionalComponentPropertyExpression: A => Option[B]) = {
    new OptionalComponentFieldRequiringSimpleValidatorRuleBuilder[A, B](optionalComponentPropertyExpression)
  }

  def ForEachComponent[B](componentListPropertyExpression: A => List[B]) = {
    new ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, B](componentListPropertyExpression)
  }
}

