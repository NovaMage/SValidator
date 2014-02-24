package com.github.novamage.svalidator.validation.binding

import com.github.novamage.svalidator.validation.ValidationFailure
import com.github.novamage.svalidator.binding.binders.special.MapToObjectBinder
import scala.reflect.runtime.{universe => ru}
import com.github.novamage.svalidator.binding.{BindingPass, BindingFailure}
import com.github.novamage.svalidator.validation.simple.SimpleValidator

abstract class MappingBindingValidator[A] extends SimpleValidator[A] {

  def bindAndValidate[B](valuesMap: Map[String, Seq[String]], mapOp: B => A)(implicit tag: ru.TypeTag[B]): BindingAndValidationSummary[A] = {
    val bindingResult = MapToObjectBinder.bind[B](valuesMap)
    bindingResult match {
      case BindingFailure(errors, cause) => BindingAndValidationSummary(errors.map(error => ValidationFailure(error.fieldName, error.errorMessage)), None, valuesMap)
      case BindingPass(value) =>
        val mappedValue = mapOp(value)
        BindingAndValidationSummary(validate(mappedValue).validationFailures, Some(mappedValue), valuesMap)
    }

  }

}
