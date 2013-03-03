package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.{ ValidationFailure, ValidationPass, IValidationRule }

class SimpleValidationRule[A, R](propertyExtractor: A => R, ruleExpression: R => Boolean, fieldName: String, errorMessage: (String, R) => String, conditionedValidation: A => Boolean) extends IValidationRule[A] {
  def apply(instance: A) = {
    lazy val propertyValue = propertyExtractor(instance)
    if (!conditionedValidation(instance) || ruleExpression(propertyValue))
      ValidationPass
    else
      ValidationFailure(fieldName, errorMessage(fieldName, propertyValue))
  }
}

