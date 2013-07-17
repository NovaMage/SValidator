package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation._

abstract class SimpleValidator[A] extends IValidate[A] {

  def buildRules: List[IRuleBuilder[A]]

  private lazy val rules = buildRules.map(_.buildRules)

  override def validate(instance: A): ValidationSummary = {
    val unflattenedValidationRuleStreams: List[Stream[IValidationRule[A]]] = rules.map(_.toStream)
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

  def ForOptionalComponent[B](optionalComponentPropertyExpression: A => Option[B]) = {
    new OptionalComponentFieldRequiringSimpleValidatorRuleBuilder[A, B](optionalComponentPropertyExpression)
  }

  def ForEachComponent[B](componentListPropertyExpression: A => List[B]) = {
    new ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, B](componentListPropertyExpression)
  }
}

