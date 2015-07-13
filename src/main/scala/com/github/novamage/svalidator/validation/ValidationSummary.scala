package com.github.novamage.svalidator.validation


class ValidationSummary(val validationFailures: List[ValidationFailure]) {

  def isValid = validationFailures.isEmpty
}

object ValidationSummary {

  def apply(validationFailures: List[ValidationFailure]): ValidationSummary = new ValidationSummary(validationFailures)

  final val Empty = new ValidationSummary(Nil)
}
