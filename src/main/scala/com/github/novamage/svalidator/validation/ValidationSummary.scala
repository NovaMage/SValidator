package com.github.novamage.svalidator.validation

case class ValidationSummary(validationFailures: List[ValidationFailure]) {

  def isValid = validationFailures.isEmpty
}
