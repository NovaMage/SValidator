package com.github.novamage.svalidator.validation

import simple.SimpleValidator

case class ValidationSummary(validationFailures: List[ValidationFailure]) {

  def isValid = validationFailures.isEmpty
}
