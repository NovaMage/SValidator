package com.github.novamage.svalidator.validation

case class ValidationSummary(validationFailures: List[ValidationResult]) {

  def isValid = validationFailures.isEmpty
}
