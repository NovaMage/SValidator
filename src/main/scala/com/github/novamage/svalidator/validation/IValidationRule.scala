package com.github.novamage.svalidator.validation

trait IValidationRule[T] {

  def apply(instance: T): ValidationResult

}
