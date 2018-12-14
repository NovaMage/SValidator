package com.github.novamage.svalidator.validation

/**
  * Contains information about errors ocurred during the validation process
  *
  * @param validationFailures List of failures that occurred during validation
  */
case class ValidationWithData[+A](validationFailures: List[ValidationFailure],
                                  data: Option[A])
  extends ValidationResult[A] {

  def merge[B](another: ValidationResult[B]): ValidationResult[List[Any]] = {
    val newData = if (data.isDefined || another.data.isDefined) {
      Some(List(data, another.data).flatten)
    } else {
      None
    }
    ValidationWithData(validationFailures ++ another.validationFailures, newData)
  }

  def mergeWithoutData(another: ValidationResult[_]): ValidationSummary = {
    ValidationSummary(validationFailures ++ another.validationFailures)
  }

  def withData[B](data: B): ValidationResult[B] = copy(data = Some(data))

  def localize(implicit localizer: Localizer): ValidationResult[A] = ValidationWithData(validationFailures.map(_.localize), data)

}

object ValidationWithData {

  final val Empty: ValidationWithData[Nothing] = ValidationWithData(Nil, None)
}