package com.github.novamage.svalidator.validation

/**
  * Contains information about errors ocurred during the validation process
  *
  * @param validationFailures List of failures that occurred during validation
  */
case class ValidationWithData[+A](validationFailures: List[ValidationFailure],
                                  data: Option[A]) {

  /** Returns true if no failures occurred
    */
  def isValid: Boolean = validationFailures.isEmpty

  /** Returns a summary with the combined failures of the current summary and the passed in summary
    *
    * @param another Summary to merge failures with
    */
  def merge[B](another: ValidationWithData[B]): ValidationWithData[List[Any]] = {
    val newData = if (data.isDefined || another.data.isDefined) {
      Some(List(data, another.data).flatten)
    } else {
      None
    }
    ValidationWithData(validationFailures ++ another.validationFailures, newData)
  }

  /** Applies the given [[com.github.novamage.svalidator.validation.Localizer Localizer]] to all failures in this summary
    *
    * @param localizer Localizer to apply to failures
    * @return A new summary with all failures localized using the given localizer
    */
  def localize(implicit localizer: Localizer): ValidationWithData[A] = ValidationWithData(validationFailures.map(_.localize), data)

}

object ValidationWithData {

  /** A summary with no failures.
    */
  final val Empty: ValidationWithData[Nothing] = ValidationWithData(Nil, None)
}
