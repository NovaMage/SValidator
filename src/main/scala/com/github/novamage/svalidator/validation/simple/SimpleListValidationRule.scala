package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation._
import com.github.novamage.svalidator.validation.simple.internals.IValidationRule

class SimpleListValidationRule[A, B](lazyPropertyExtractor: => List[B],
                                     ruleExpression: (B, A) => Boolean,
                                     fieldName: String,
                                     errorMessageKey: Option[String],
                                     errorMessageFormatValues: Option[B => List[Any]],
                                     conditionedValidation: A => Boolean,
                                     markIndexesOfFieldNameErrors: Boolean,
                                     metadata: Map[String, List[Any]]) extends IValidationRule[A] {

  override def apply(instance: A): List[ValidationFailure] = {
    if (!conditionedValidation(instance))
      Nil
    else {
      lazyPropertyExtractor.zipWithIndex.collect {
        case (propertyValue, index) if !ruleExpression(propertyValue, instance) =>
          val indexString = if (markIndexesOfFieldNameErrors) "[" + index + "]" else ""
          val formatValues = errorMessageFormatValues.map(_.apply(propertyValue)).getOrElse(List(propertyValue))
          val messageParts = MessageParts(
            messageKey = errorMessageKey.getOrElse("invalid.value"),
            messageFormatValues = formatValues)
          ValidationFailure(fieldName + indexString, messageParts, metadata)
      }
    }

  }
}
