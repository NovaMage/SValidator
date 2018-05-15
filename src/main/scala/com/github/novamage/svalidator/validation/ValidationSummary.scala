package com.github.novamage.svalidator.validation

case class ValidationSummary(validationFailures: List[ValidationFailure]) {

  def isValid: Boolean = validationFailures.isEmpty

  def merge(another: ValidationSummary): ValidationSummary = {
    ValidationSummary(validationFailures ++ another.validationFailures)
  }

  def localize(implicit localizer: Localizer): ValidationSummary = ValidationSummary(validationFailures.map(_.localize))

}

object ValidationSummary {

  final val Empty: ValidationSummary = ValidationSummary(Nil)
}
