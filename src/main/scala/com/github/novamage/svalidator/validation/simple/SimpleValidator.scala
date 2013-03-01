package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation._

abstract class SimpleValidator[T] extends IValidate[T] {

  def buildRules: List[IRuleBuilder[T]]

  override def validate(instance: T) = {
    val rules = buildRules
    val unflattenedValidationRules = rules.map(_.buildRules.toStream)
    val firstFailingResultForEachGroup = unflattenedValidationRules.map(ruleStream =>
      ruleStream.map(_.apply(instance)).collectFirst {
        case result: ValidationFailure => result
      }).filter(_.isDefined).map(_.get)
    ValidationSummary(firstFailingResultForEachGroup)
  }

  def For[R](propertyExpression: T => R) = {
    new FieldRequiringSimpleValidationRuleBuilder[T, R](propertyExpression, Nil, Nil)
  }




}

