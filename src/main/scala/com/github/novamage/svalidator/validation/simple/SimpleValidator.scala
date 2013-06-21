package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation._

abstract class SimpleValidator[A] extends IValidate[A] {

  def buildRules: List[IRuleBuilder[A]]

  private lazy val rules = buildRules.map(_.buildRules)

  override def validate(instance: A): ValidationSummary = {
    val unflattenedValidationRuleStreams = rules.map(_.toStream)
    val firstFailingResultForEachGroup =
      unflattenedValidationRuleStreams map {
        ruleStream =>
          ruleStream map {_.apply(instance)} collectFirst {
            case result: ValidationFailure => result
          }
      } collect {
        case Some(x) => x
      }
    new ValidationSummary(firstFailingResultForEachGroup)
  }

  def For[B](propertyExpression: A => B) = {
    new FieldRequiringSimpleValidationRuleBuilder[A, B](propertyExpression)
  }


}

