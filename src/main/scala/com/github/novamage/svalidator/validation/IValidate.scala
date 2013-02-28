package com.github.novamage.svalidator.validation

trait IValidate[T] {

  def validate(instance: T): ValidationSummary

}
