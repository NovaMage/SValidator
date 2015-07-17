package com.github.novamage.svalidator.validation


class ValidationSummary(val validationFailures: List[ValidationFailure],
                        val metadata: Map[String, Any] = Map.empty[String, Any]) {

  def isValid = validationFailures.isEmpty

  def merge(another: ValidationSummary): ValidationSummary = {
    val otherMetadata = another.metadata
    val allKeys = metadata.keySet ++ otherMetadata.keySet
    val mergedMetadata = allKeys.map { key =>
      if (metadata.contains(key) && otherMetadata.contains(key)) {
        key -> List(metadata(key), otherMetadata(key))
      } else if (metadata.contains(key)) {
        key -> metadata(key)
      } else {
        key -> otherMetadata(key)
      }
    }
    new ValidationSummary(validationFailures ++ another.validationFailures, mergedMetadata.toMap)
  }

}

object ValidationSummary {

  def apply(validationFailures: List[ValidationFailure], metadata: (String, Any)*) = new ValidationSummary(validationFailures, metadata.toMap)

  final val Empty: ValidationSummary = new ValidationSummary(Nil)
}
