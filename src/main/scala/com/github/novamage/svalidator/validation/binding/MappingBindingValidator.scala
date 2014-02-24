package com.github.novamage.svalidator.validation.binding

import com.github.novamage.svalidator.validation.ValidationFailure
import com.github.novamage.svalidator.binding.binders.special.MapToObjectBinder
import scala.reflect.runtime.{universe => ru}
import com.github.novamage.svalidator.binding.{BindingPass, BindingFailure}
import com.github.novamage.svalidator.validation.simple.SimpleValidator

abstract class MappingBindingValidator[A: ru.TypeTag, B] extends SimpleValidator[B] {

  def bindAndValidate(valuesMap: Map[String, Seq[String]], mapOp: A => B): BindingAndValidationSummary[B] = {
    val bindingResult = MapToObjectBinder.bind[A](valuesMap)
    bindingResult match {
      case BindingFailure(errors, cause) => BindingAndValidationSummary(errors.map(error => ValidationFailure(error.fieldName, error.errorMessage)), None, valuesMap)
      case BindingPass(value) =>
        val mappedValue = mapOp(value)
        BindingAndValidationSummary(validate(mappedValue).validationFailures, Some(mappedValue), valuesMap)
    }

  }

}
