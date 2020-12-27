package com.github.novamage.svalidator.validation.binding

import com.github.novamage.svalidator.binding.binders.special.{JsonToObjectBinder, MapToObjectBinder}
import com.github.novamage.svalidator.binding.{BindingFailure, BindingPass, TypeBinderRegistry}
import com.github.novamage.svalidator.validation.ValidationFailure
import com.github.novamage.svalidator.validation.simple.SimpleValidatorWithData
import io.circe.Json

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
    * @param valuesMap       Values map to use for binding
    * @param mapOp           The transformation function from the bound value's type to the validated value's type
    * @param bindingMetadata Additional values passed as metadata for binding
    * @tparam C Type of the instance being bound
    * @return A summary of field errors or validation failures if any ocurred, or a summary containing the bound instance
    *         otherwise.
    */
  def bindAndValidate[C](valuesMap: Map[String, Seq[String]], mapOp: C => A, bindingMetadata: Map[String, Any])(implicit tag: ru.TypeTag[C]): BindingAndValidationWithData[A, B] = {
    val registry = TypeBinderRegistry
    registry.beforeBindingAndValidationHooks.foreach(_.beforeBind(Some(valuesMap), None, bindingMetadata)(tag))
    val bindingResult = MapToObjectBinder.bind[C](valuesMap, bindingMetadata = bindingMetadata)
    bindingResult match {
      case BindingFailure(errors, _) =>
        registry.failedBindingHooks.foreach(_.onFailedBind(errors, Some(valuesMap), None, bindingMetadata)(tag))
        Failure(errors.map(error => ValidationFailure(error.fieldName, error.messageParts, Map.empty)), Some(valuesMap), None, None, None)
      case BindingPass(value) =>
        val mappedValue = mapOp(value)
        val validatedValue = validate(mappedValue)
        if (validatedValue.isValid) {
          registry.successfulBindingAndValidationHooks.foreach(_.onSuccess(mappedValue, Some(valuesMap), None, bindingMetadata))
          Success(mappedValue, Some(valuesMap), None, validatedValue.data)
        }
        else {
          registry.successfulBindingFailedValidationHooks.foreach(_.onFailedValidation(mappedValue, validatedValue.validationFailures, Some(valuesMap), None, bindingMetadata))
          Failure(validatedValue.validationFailures, Some(valuesMap), None, Some(mappedValue), validatedValue.data)
        }
    }
  }

  /** Attempts to perform binding and validation of the given type using the specified json.
    *
    * This method calls
    * [[com.github.novamage.svalidator.binding.binders.special.JsonToObjectBinder#bind JsonToObjectBinder.bind]] method to
    * perform the binding, and, if successful, applies <code>mapOp</code> to it, and then calls <code>validate</code>
    * on the transformed value.  If not, field errors are converted to validation failures and returned in the summary
    *
    * @param json            Json to use for binding
    * @param mapOp           The transformation function from the bound value's type to the validated value's type
    * @param bindingMetadata Additional values passed as metadata for binding
    * @tparam C Type of the instance being bound
    * @return A summary of field errors or validation failures if any ocurred, or a summary containing the bound instance
    *         otherwise.
    */
  def bindAndValidate[C](json: Json, mapOp: C => A, bindingMetadata: Map[String, Any])(implicit tag: ru.TypeTag[C]): BindingAndValidationWithData[A, B] = {
    val registry = TypeBinderRegistry
    registry.beforeBindingAndValidationHooks.foreach(_.beforeBind(None, Some(json), bindingMetadata)(tag))
    val bindingResult = JsonToObjectBinder.bind[C](json, bindingMetadata = bindingMetadata)
    bindingResult match {
      case BindingFailure(errors, _) =>
        registry.failedBindingHooks.foreach(_.onFailedBind(errors, None, Some(json), bindingMetadata)(tag))
        Failure(errors.map(error => ValidationFailure(error.fieldName, error.messageParts, Map.empty)), None, Some(json), None, None)
      case BindingPass(value) =>
        val mappedValue = mapOp(value)
        val validatedValue = validate(mappedValue)
        if (validatedValue.isValid) {
          registry.successfulBindingAndValidationHooks.foreach(_.onSuccess(mappedValue, None, Some(json), bindingMetadata))
          Success(mappedValue, None, Some(json), validatedValue.data)
        }
        else {
          registry.successfulBindingFailedValidationHooks.foreach(_.onFailedValidation(mappedValue, validatedValue.validationFailures, None, Some(json), bindingMetadata))
          Failure(validatedValue.validationFailures, None, Some(json), Some(mappedValue), validatedValue.data)
        }
    }
  }

  /** Attempts to perform binding and validation of the given type using the specified json string.
    *
    * This method calls
    * [[com.github.novamage.svalidator.binding.binders.special.JsonToObjectBinder#bind JsonToObjectBinder.bind]] method to
    * perform the binding, and, if successful, applies <code>mapOp</code> to it, and then calls <code>validate</code>
    * on the transformed value.  If not, field errors are converted to validation failures and returned in the summary
    *
    * @param jsonString      String to use for json binding
    * @param mapOp           The transformation function from the bound value's type to the validated value's type
    * @param bindingMetadata Additional values passed as metadata for binding
    * @tparam C Type of the instance being bound
    * @return A summary of field errors or validation failures if any ocurred, or a summary containing the bound instance
    *         otherwise.
    */
  def bindAndValidate[C](jsonString: String, mapOp: C => A, bindingMetadata: Map[String, Any])(implicit tag: ru.TypeTag[C]): BindingAndValidationWithData[A, B] = {
    val registry = TypeBinderRegistry
    io.circe.parser.parse(jsonString) match {
      case Left(parsingFailure) =>
        Failure(List(ValidationFailure.apply("", TypeBinderRegistry.currentBindingConfig.languageConfig.invalidJsonMessage(None, jsonString), Map("exception" -> List(parsingFailure.getMessage())))), None, None, None, None)
      case Right(json) =>
        registry.beforeBindingAndValidationHooks.foreach(_.beforeBind(None, Some(json), bindingMetadata)(tag))
        val bindingResult = JsonToObjectBinder.bind[C](json, bindingMetadata = bindingMetadata)
        bindingResult match {
          case BindingFailure(errors, _) =>
            registry.failedBindingHooks.foreach(_.onFailedBind(errors, None, Some(json), bindingMetadata)(tag))
            Failure(errors.map(error => ValidationFailure(error.fieldName, error.messageParts, Map.empty)), None, Some(json), None, None)
          case BindingPass(value) =>
            val mappedValue = mapOp(value)
            val validatedValue = validate(mappedValue)
            if (validatedValue.isValid) {
              registry.successfulBindingAndValidationHooks.foreach(_.onSuccess(mappedValue, None, Some(json), bindingMetadata))
              Success(mappedValue, None, Some(json), validatedValue.data)
            }
            else {
              registry.successfulBindingFailedValidationHooks.foreach(_.onFailedValidation(mappedValue, validatedValue.validationFailures, None, Some(json), bindingMetadata))
              Failure(validatedValue.validationFailures, None, Some(json), Some(mappedValue), validatedValue.data)
            }
        }
    }
  }


}

