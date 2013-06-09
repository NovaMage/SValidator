package com.github.novamage.svalidator.validation


class ValidationSummary(val validationFailures: List[ValidationFailure]) {

  def isValid = validationFailures.isEmpty
}
