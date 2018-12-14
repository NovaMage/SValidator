package com.github.novamage.svalidator.validation.simple

abstract class SimpleValidator[A]
  extends SimpleValidatorWithData[A, Nothing]
    with ValidatorWithoutData[A] {


}
