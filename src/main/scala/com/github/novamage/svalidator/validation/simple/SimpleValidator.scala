package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation._

abstract class SimpleValidator[A] extends IValidate[A] {

  def buildRules: List[IRuleBuilder[A]]

  override def validate(instance: A) = {
    val rules = buildRules
    val unflattenedValidationRuleStreams = rules map {_.buildRules.toStream}
    val firstFailingResultForEachGroup =
      unflattenedValidationRuleStreams map {
        ruleStream =>
          ruleStream map {_.apply(instance)} collectFirst {
            case result: ValidationFailure => result
          }
      } collect {
        case Some(x) => x
      }
    ValidationSummary(firstFailingResultForEachGroup)
  }

  def For[B](propertyExpression: A => B): FieldRequiringSimpleValidationRuleBuilder[A, B] = {
    new FieldRequiringSimpleValidationRuleBuilder[A, B](propertyExpression)
  }

}

