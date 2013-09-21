package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.{ValidationFailure, IValidationRule}

class SimpleOptionValidationRule[A, B](lazyPropertyValue: => Option[B], ruleExpression: (B, A) => Boolean, fieldName: String, errorMessage: (String, B) => String, conditionedValidation: A => Boolean) extends IValidationRule[A] {
  override def apply(instance: A): List[ValidationFailure] = {
    if (!conditionedValidation(instance))
      Nil
    else {
      lazyPropertyValue.toList.collect {
        case propertyValue if !ruleExpression(propertyValue, instance) => ValidationFailure(fieldName, errorMessage(fieldName, propertyValue))
      }
    }

  }
}

