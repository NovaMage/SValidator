package com.github.novamage.svalidator.validation

/**
  * Contains information about errors ocurred during the validation process
  *
  * @param validationFailures List of failures that occurred during validation
  */
case class ValidationSummary(validationFailures: List[ValidationFailure])
  extends ValidationResult[Nothing] {

  def localize(implicit localizer: Localizer): ValidationSummary = {
    ValidationSummary(validationFailures.map(_.localize))
  }

  override def data: Option[Nothing] = None
}

object ValidationSummary {

  final val Empty: ValidationSummary = ValidationSummary(Nil)

}
