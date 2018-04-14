package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.{Constants, IValidationRule, ValidationFailure}

class SimpleListValidationRule[A, B](lazyPropertyExtractor: => List[B],
                                     ruleExpression: (B, A) => Boolean,
                                     fieldName: String,
                                     errorMessage: (A, B) => String,
                                     conditionedValidation: A => Boolean,
                                     markIndexesOfFieldNameErrors: Boolean,
                                     metadata: Map[String, List[Any]]) extends IValidationRule[A] {

  override def apply(instance: A): List[ValidationFailure] = {
    if (!conditionedValidation(instance))
      Nil
    else {
      lazyPropertyExtractor.zipWithIndex.collect {
        case (propertyValue, index) if !ruleExpression(propertyValue, instance) =>
          val indexString = if (markIndexesOfFieldNameErrors) "[" + index + "]" else Constants.emptyString
          ValidationFailure(fieldName + indexString, errorMessage(instance, propertyValue), metadata)
      }
    }

  }
}
