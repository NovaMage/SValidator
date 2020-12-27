package com.github.novamage.svalidator.validation.binding

import io.circe.Json

import scala.reflect.runtime.{universe => ru}

/** Base class that provides binding alongside validation of a
  * [[com.github.novamage.svalidator.validation.simple.SimpleValidatorWithData SimpleValidator]]
  *
  * @tparam A Type of the instance that will be bound and validated
  */
abstract class BindingValidatorWithData[A: ru.TypeTag, B] extends MappingBindingValidatorWithData[A, B] {

  /** Attempts to perform binding and validation of the given type using the specified values map.
    *
    * This method calls
    * [[com.github.novamage.svalidator.binding.binders.special.MapToObjectBinder#bind MapToObjectBinder.bind]] method to
    * perform the binding, and, if successful, calls <code>validate</code> on it.  If not, field errors are converted
    * to validation failures and returned in the summary
    *
    * @param valuesMap       Values map to use for binding
    * @param bindingMetadata Additional values passed as metadata for binding
    * @tparam C Type of the instance being bound and validated
    * @return A summary of field errors or validation failures if any ocurred, or a summary containing the bound instance
    *         otherwise.
    */
  def bindAndValidate[C](valuesMap: Map[String, Seq[String]], bindingMetadata: Map[String, Any])(implicit tag: ru.TypeTag[C]): BindingAndValidationWithData[A, B] = {
    super.bindAndValidate(valuesMap, (x: A) => x, bindingMetadata)
  }

  /** Attempts to perform binding and validation of the given type using the json AST.
    *
    * This method calls
    * [[com.github.novamage.svalidator.binding.binders.special.JsonToObjectBinder#bind JsonToObjectBinder.bind]] method to
    * perform the binding, and, if successful, calls <code>validate</code> on it.  If not, field errors are converted
    * to validation failures and returned in the summary
    *
    * @param json            The json to use for binding
    * @param bindingMetadata Additional values passed as metadata for binding
    * @tparam C Type of the instance being bound and validated
    * @return A summary of field errors or validation failures if any ocurred, or a summary containing the bound instance
    *         otherwise.
    */
  def bindAndValidate[C](json: Json, bindingMetadata: Map[String, Any])(implicit tag: ru.TypeTag[C]): BindingAndValidationWithData[A, B] = {
    super.bindAndValidate(json, (x: A) => x, bindingMetadata)
  }

  /** Attempts to perform binding and validation of the given type using the json string.
    *
    * This method calls
    * [[com.github.novamage.svalidator.binding.binders.special.JsonToObjectBinder#bind JsonToObjectBinder.bind]] method to
    * perform the binding, and, if successful, calls <code>validate</code> on it.  If not, field errors are converted
    * to validation failures and returned in the summary
    *
    * @param jsonString      The string to use for json binding
    * @param bindingMetadata Additional values passed as metadata for binding
    * @tparam C Type of the instance being bound and validated
    * @return A summary of field errors or validation failures if any ocurred, or a summary containing the bound instance
    *         otherwise.
    */
  def bindAndValidate[C](jsonString: String, bindingMetadata: Map[String, Any])(implicit tag: ru.TypeTag[C]): BindingAndValidationWithData[A, B] = {
    super.bindAndValidate(jsonString, (x: A) => x, bindingMetadata)
  }

}
