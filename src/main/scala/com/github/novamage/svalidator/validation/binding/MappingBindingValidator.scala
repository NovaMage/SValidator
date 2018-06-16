package com.github.novamage.svalidator.validation.binding

import com.github.novamage.svalidator.binding.binders.special.MapToObjectBinder
import com.github.novamage.svalidator.binding.{BindingFailure, BindingPass}
import com.github.novamage.svalidator.validation.ValidationFailure
import com.github.novamage.svalidator.validation.simple.SimpleValidator

import scala.reflect.runtime.{universe => ru}

/** Base class that provides binding, plus a transformation, and then validation of a
  * [[com.github.novamage.svalidator.validation.simple.SimpleValidator SimpleValidator]]
  *
  * @tparam A Type of the instance that will be validated
  */
abstract class MappingBindingValidator[A] extends SimpleValidator[A] {

  /** Attempts to perform binding and validation of the given type using the specified values map.
    *
    * This method calls
    * [[com.github.novamage.svalidator.binding.binders.special.MapToObjectBinder#bind MapToObjectBinder.bind]] method to
    * perform the binding, and, if successful, applies <code>mapOp</code> to it, and then calls <code>validate</code>
    * on the transformed value.  If not, field errors are converted to validation failures and returned in the summary
    *
    * @param valuesMap Values map to use for binding
    * @param mapOp The transformation function from the bound value's type to the validated value's type
    * @tparam B Type of the instance being bound
    * @return A summary of field errors or validation failures if any ocurred, or a summary containing the bound instance
    *         otherwise.
    */
  def bindAndValidate[B](valuesMap: Map[String, Seq[String]], mapOp: B => A)(implicit tag: ru.TypeTag[B]): BindingAndValidationSummary[A] = {
    val bindingResult = MapToObjectBinder.bind[B](valuesMap)
    bindingResult match {
      case BindingFailure(errors, _) => Failure(errors.map(error => ValidationFailure(error.fieldName, error.messageParts, Map.empty)), valuesMap, None)
      case BindingPass(value) =>
        val mappedValue = mapOp(value)
        val validatedValue = validate(mappedValue)
        if (validatedValue.isValid)
          Success(mappedValue, valuesMap)
        else
          Failure(validatedValue.validationFailures, valuesMap, Some(mappedValue))
    }
  }


}

