package com.github.novamage.svalidator.validation

class ValidationSummary(val validationFailures: List[ValidationFailure]) {

  def isValid: Boolean = validationFailures.isEmpty

  def merge(another: ValidationSummary): ValidationSummary = {
    new ValidationSummary(validationFailures ++ another.validationFailures)
  }

}

object ValidationSummary {

  def apply(validationFailures: List[ValidationFailure]) = new ValidationSummary(validationFailures)

  final val Empty: ValidationSummary = new ValidationSummary(Nil)
}
