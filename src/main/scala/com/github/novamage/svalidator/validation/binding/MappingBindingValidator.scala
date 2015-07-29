package com.github.novamage.svalidator.validation.binding

import com.github.novamage.svalidator.binding.binders.special.MapToObjectBinder
import com.github.novamage.svalidator.binding.{BindingFailure, BindingPass}
import com.github.novamage.svalidator.validation.ValidationFailure
import com.github.novamage.svalidator.validation.simple.SimpleValidator

import scala.reflect.runtime.{universe => ru}

abstract class MappingBindingValidator[A] extends SimpleValidator[A] {


  def bindAndValidate[B](valuesMap: Map[String, Seq[String]], mapOp: B => A)(implicit tag: ru.TypeTag[B]): BindingAndValidationSummary[A] = {
    val bindingResult = MapToObjectBinder.bind[B](valuesMap)
    bindingResult match {
      case BindingFailure(errors, cause) => Failure(errors.map(error => ValidationFailure(error.fieldName, error.errorMessage)), valuesMap, None, Map.empty[String, List[String]])
      case BindingPass(value) =>
        val mappedValue = mapOp(value)
        val validatedValue = validate(mappedValue)
        if (validatedValue.isValid)
          Success(mappedValue, valuesMap, validatedValue.metadata)
        else
          Failure(validatedValue.validationFailures, valuesMap, Some(mappedValue), validatedValue.metadata)
    }

  }

}

