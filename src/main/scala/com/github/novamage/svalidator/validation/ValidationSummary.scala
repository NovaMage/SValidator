package com.github.novamage.svalidator.validation

import com.github.novamage.svalidator.utils.Utils


class ValidationSummary(val validationFailures: List[ValidationFailure],
                        val metadata: Map[String, List[Any]] = Map.empty[String, List[Any]]) {

  def isValid = validationFailures.isEmpty

  def merge(another: ValidationSummary): ValidationSummary = {
    val mergedMetadata = Utils.mergeMaps(metadata, another.metadata)
    new ValidationSummary(validationFailures ++ another.validationFailures, mergedMetadata.toMap)
  }

}

object ValidationSummary {

  def apply(validationFailures: List[ValidationFailure], metadata: (String, List[Any])*) = new ValidationSummary(validationFailures, metadata.toMap)

  final val Empty: ValidationSummary = new ValidationSummary(Nil)
}
