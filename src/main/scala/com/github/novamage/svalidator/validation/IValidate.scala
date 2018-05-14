package com.github.novamage.svalidator.validation

trait IValidate[-A] {

  def validate(implicit instance: A, localizer: Localizer): ValidationSummary

}

