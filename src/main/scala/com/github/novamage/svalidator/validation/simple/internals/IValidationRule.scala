package com.github.novamage.svalidator.validation.simple.internals

import com.github.novamage.svalidator.validation.ValidationFailure

trait IValidationRule[-A] {

  def apply(instance: A): List[ValidationFailure]

}

