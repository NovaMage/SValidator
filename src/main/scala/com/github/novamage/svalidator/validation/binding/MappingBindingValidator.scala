package com.github.novamage.svalidator.validation.binding

import com.github.novamage.svalidator.validation.simple.ValidatorWithoutData

abstract class MappingBindingValidator[A]
  extends MappingBindingValidatorWithData[A, Nothing]
    with ValidatorWithoutData[A] {

}
