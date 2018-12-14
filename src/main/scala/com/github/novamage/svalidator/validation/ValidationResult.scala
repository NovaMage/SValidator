package com.github.novamage.svalidator.validation

trait ValidationResult[+A] {

  def validationFailures: List[ValidationFailure]

  def data: Option[A]

  /** Returns true if no failures occurred
    */
  def isValid: Boolean = validationFailures.isEmpty

  /** Returns a summary with the combined failures of the current summary and the passed in summary
    *
    * @param another Summary to merge failures with
    */
  def merge[B](another: ValidationResult[B]): ValidationResult[List[Any]]

  def mergeWithoutData(another: ValidationResult[_]): ValidationSummary

  def withData[B](data: B): ValidationResult[B]

  /** Applies the given [[com.github.novamage.svalidator.validation.Localizer Localizer]] to all failures in this summary
    *
    * @param localizer Localizer to apply to failures
    * @return A new summary with all failures localized using the given localizer
    */
  def localize(implicit localizer: Localizer): ValidationResult[A]

}

object ValidationResult {

  final val Empty: ValidationResult[Nothing] = ValidationSummary(Nil)
}
