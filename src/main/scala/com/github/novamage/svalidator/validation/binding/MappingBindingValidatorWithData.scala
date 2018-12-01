package com.github.novamage.svalidator.validation.binding

import com.github.novamage.svalidator.binding.binders.special.MapToObjectBinder
import com.github.novamage.svalidator.binding.{BindingFailure, BindingPass}
import com.github.novamage.svalidator.validation.ValidationFailure
import com.github.novamage.svalidator.validation.simple.SimpleValidatorWithData

import scala.reflect.runtime.{universe => ru}

/** Base class that provides binding, plus a transformation, and then validation of a
  * [[com.github.novamage.svalidator.validation.simple.SimpleValidatorWithData SimpleValidator]]
  *
  * @tparam A Type of the instance that will be validated
  */
abstract class MappingBindingValidatorWithData[A, B] extends SimpleValidatorWithData[A, B] {

  /** Attempts to perform binding and validation of the given type using the specified values map.
    *
    * This method calls
    * [[com.github.novamage.svalidator.binding.binders.special.MapToObjectBinder#bind MapToObjectBinder.bind]] method to
    * perform the binding, and, if successful, applies <code>mapOp</code> to it, and then calls <code>validate</code>
    * on the transformed value.  If not, field errors are converted to validation failures and returned in the summary
    *
    * @param valuesMap Values map to use for binding
    * @param mapOp     The transformation function from the bound value's type to the validated value's type
    * @param bindingMetadata Additional values passed as metadata for binding
    * @tparam C Type of the instance being bound
    * @return A summary of field errors or validation failures if any ocurred, or a summary containing the bound instance
    *         otherwise.
    */
  def bindAndValidate[C](valuesMap: Map[String, Seq[String]], mapOp: C => A, bindingMetadata: Map[String, Any])(implicit tag: ru.TypeTag[C]): BindingAndValidationWithData[A, B] = {
    val bindingResult = MapToObjectBinder.bind[C](valuesMap, bindingMetadata = bindingMetadata)
    bindingResult match {
      case BindingFailure(errors, _) => Failure(errors.map(error => ValidationFailure(error.fieldName, error.messageParts, Map.empty)), valuesMap, None, None)
      case BindingPass(value) =>
        val mappedValue = mapOp(value)
        val validatedValue = validate(mappedValue)
        if (validatedValue.isValid)
          Success(mappedValue, valuesMap, validatedValue.data)
        else
          Failure(validatedValue.validationFailures, valuesMap, Some(mappedValue), validatedValue.data)
    }
  }


}

