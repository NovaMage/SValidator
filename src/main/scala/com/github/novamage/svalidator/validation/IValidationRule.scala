package com.github.novamage.svalidator.validation

trait IValidationRule[-A] {

  def apply(instance: A, localizer: Localizer): List[ValidationFailure]

}

