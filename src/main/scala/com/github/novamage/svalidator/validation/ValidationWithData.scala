package com.github.novamage.svalidator.validation

/**
  * Contains information about errors occurred during the validation process
  *
  * @param validationFailures List of failures that occurred during validation
  * @param data Additional data to be stored along the validation result
  */
case class ValidationWithData[+A](validationFailures: List[ValidationFailure],
                                  data: Option[A])
  extends ValidationResult[A] {

  def localize(implicit localizer: Localizer): ValidationResult[A] = ValidationWithData(validationFailures.map(_.localize), data)

}

object ValidationWithData {

  final val Empty: ValidationWithData[Nothing] = ValidationWithData(Nil, None)
}