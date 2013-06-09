package com.github.novamage.svalidator.validation.binding

import com.github.novamage.svalidator.validation.simple.SimpleValidator
import scala.reflect.runtime.{universe => ru}
import com.github.novamage.svalidator.binding.binders.special.MapToObjectBinder
import com.github.novamage.svalidator.validation.ValidationFailure
import com.github.novamage.svalidator.binding.{BindingPass, BindingFailure}

abstract class BindingValidator[A: ru.TypeTag] extends SimpleValidator[A] with IBindingValidator[A] {

  def bindAndValidate(valuesMap: Map[String, Seq[String]]): BindingAndValidationSummary[A] = {
    val bindingResult = MapToObjectBinder.bind[A](valuesMap)
    bindingResult match {
      case BindingFailure(errors) => BindingAndValidationSummary(errors.map(error => ValidationFailure(error.fieldName, error.errorMessage)), None)
      case BindingPass(value) => BindingAndValidationSummary(validate(value).validationFailures, Some(value))
    }

  }

}
