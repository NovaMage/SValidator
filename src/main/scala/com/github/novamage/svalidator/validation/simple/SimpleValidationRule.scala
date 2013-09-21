package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.{ValidationFailure, IValidationRule}

class SimpleValidationRule[A, B](lazyPropertyValue: => B, ruleExpression: B => Boolean, fieldName: String, errorMessage: (String, B) => String, conditionedValidation: A => Boolean) extends IValidationRule[A] {
  def apply(instance: A) = {
    if (!conditionedValidation(instance) || ruleExpression(lazyPropertyValue))
      Nil
    else
      List(ValidationFailure(fieldName, errorMessage(fieldName, lazyPropertyValue)))
  }
}

