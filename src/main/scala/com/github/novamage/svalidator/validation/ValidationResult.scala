package com.github.novamage.svalidator.validation

/**
  * Provides information about errors occurred during the validation process
  */
trait ValidationResult[+A] {

  def validationFailures: List[ValidationFailure]

  def data: Option[A]

  /** Returns true if no failures occurred
    */
  def isValid: Boolean = validationFailures.isEmpty

  /** Returns a summary with the combined failures of the current summary and the passed in summary
    * and a list of their combined data
    *
    * @param another Summary to merge failures with
    */
  def merge[B](another: ValidationResult[B]): ValidationResult[List[Any]] = {
    val newData = if (data.isDefined || another.data.isDefined) {
      Some(List(data, another.data).flatten)
    } else {
      None
    }
    ValidationWithData(validationFailures ++ another.validationFailures, newData)
  }

  /** Returns a summary with the combined failures of the current summary and the passed in summary
    * without any data
    *
    * @param another Summary to merge failures with
    */
  def mergeWithoutData(another: ValidationResult[_]): ValidationSummary = {
    ValidationSummary(validationFailures ++ another.validationFailures)
  }

  def withData[B](data: B): ValidationResult[B] = ValidationWithData(validationFailures, Some(data))

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
