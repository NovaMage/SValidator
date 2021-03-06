package com.github.novamage.svalidator.validation.binding

import com.github.novamage.svalidator.validation.simple.ValidatorWithoutData

import scala.reflect.runtime.{universe => ru}

abstract class BindingValidator[A: ru.TypeTag]
  extends BindingValidatorWithData[A, Nothing]
    with ValidatorWithoutData[A] {


}
