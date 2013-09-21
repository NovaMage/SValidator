package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.{ValidationFailure, IValidationRule}

class SimpleListValidationRule[A, B](lazyPropertyExtractor: => List[B], ruleExpression: (B, A) => Boolean, fieldName: String, errorMessage: (String, B) => String, conditionedValidation: A => Boolean) extends IValidationRule[A] {
  override def apply(instance: A): List[ValidationFailure] = {
    if (!conditionedValidation(instance))
      Nil
    else {
      lazyPropertyExtractor.zipWithIndex.collect {
        case (propertyValue, index) if !ruleExpression(propertyValue, instance) => ValidationFailure(fieldName + "[" + index + "]", errorMessage(fieldName, propertyValue))
      }
    }

  }
}
