package com.github.novamage.svalidator.validation.binding

import scala.reflect.runtime.{universe => ru}

abstract class BindingValidator[A: ru.TypeTag] extends BindingValidatorWithData[A, Nothing] {

}
