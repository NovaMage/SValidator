package com.github.novamage.svalidator.validation

trait IValidate[T] {

  def validate(implicit instance: T): ValidationSummary

}
