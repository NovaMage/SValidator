package com.github.novamage.svalidator.validation

/**
  * Contains information about errors ocurred during the validation process
  * @param validationFailures List of failures that occurred during validation
  */
case class ValidationSummary(validationFailures: List[ValidationFailure]) {

  /** Returns true if no failures occurred
    */
  def isValid: Boolean = validationFailures.isEmpty

  /** Returns a summary with the combined failures of the current summary and the passed in summary
    *
    * @param another Summary to merge failures with
    */
  def merge(another: ValidationSummary): ValidationSummary = {
    ValidationSummary(validationFailures ++ another.validationFailures)
  }

  /** Applies the given [[com.github.novamage.svalidator.validation.Localizer Localizer]] to all failures in this summary
    *
    * @param localizer Localizer to apply to failures
    * @return A new summary with all failures localized using the given localizer
    */
  def localize(implicit localizer: Localizer): ValidationSummary = ValidationSummary(validationFailures.map(_.localize))

}

object ValidationSummary {

  /** A summary with no failures.
    */
  final val Empty: ValidationSummary = ValidationSummary(Nil)
}
