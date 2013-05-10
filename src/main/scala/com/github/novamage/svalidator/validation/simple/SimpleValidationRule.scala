package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.{ ValidationFailure, ValidationPass, IValidationRule }

class SimpleValidationRule[A, B](propertyExtractor: A => B, ruleExpression: B => Boolean, fieldName: String, errorMessage: (String, B) => String, conditionedValidation: A => Boolean) extends IValidationRule[A] {
  def apply(instance: A) = {
    lazy val propertyValue = propertyExtractor(instance)
    if (!conditionedValidation(instance) || ruleExpression(propertyValue))
      ValidationPass
    else
      ValidationFailure(fieldName, errorMessage(fieldName, propertyValue))
  }
}

