package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.{Constants, IValidationRule, Localizer, ValidationFailure}

class SimpleListValidationRule[A, B](lazyPropertyExtractor: => List[B],
                                     ruleExpression: (B, A) => Boolean,
                                     fieldName: String,
                                     errorMessageKey: Option[String],
                                     errorMessageFormatValues: Option[B => List[Any]],
                                     conditionedValidation: A => Boolean,
                                     markIndexesOfFieldNameErrors: Boolean,
                                     metadata: Map[String, List[Any]]) extends IValidationRule[A] {

  override def apply(instance: A, localizer: Localizer): List[ValidationFailure] = {
    if (!conditionedValidation(instance))
      Nil
    else {
      lazyPropertyExtractor.zipWithIndex.collect {
        case (propertyValue, index) if !ruleExpression(propertyValue, instance) =>
          val indexString = if (markIndexesOfFieldNameErrors) "[" + index + "]" else Constants.emptyString
          val message = errorMessageKey.map { key =>
            val formatValues = errorMessageFormatValues.map(_.apply(propertyValue)).getOrElse(List(propertyValue))
            localizer.localize(key).format(formatValues: _*)
          }.getOrElse("The value %s is not a valid value for %s".format(propertyValue.toString, fieldName))
          ValidationFailure(fieldName + indexString, message, metadata)
      }
    }

  }
}
