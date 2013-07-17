package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.{ValidationFailure, IValidationRule}

class SimpleOptionValidationRule[A, B](propertyExtractor: A => Option[B], ruleExpression: B => Boolean, fieldName: String, errorMessage: (String, B) => String, conditionedValidation: A => Boolean) extends IValidationRule[A] {
  override def apply(instance: A): List[ValidationFailure] = {
    if (!conditionedValidation(instance))
      Nil
    else {
      val propertyValues = propertyExtractor(instance)
      propertyValues.toList.collect {
        case propertyValue if !ruleExpression(propertyValue) => ValidationFailure(fieldName, errorMessage(fieldName, propertyValue))
      }
    }

  }
}

