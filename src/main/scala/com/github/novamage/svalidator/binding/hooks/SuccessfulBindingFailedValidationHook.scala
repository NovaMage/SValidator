package com.github.novamage.svalidator.binding.hooks

import com.github.novamage.svalidator.validation.ValidationFailure
import io.circe.Json

trait SuccessfulBindingFailedValidationHook {

  /**
    * Trait for hooking behavior to calls of
    * [[com.github.novamage.svalidator.validation.binding.MappingBindingValidatorWithData#bindAndValidate MappingBindingValidatorWithData.bindAndValidate]]
    * and its subclasses after binding was successful but validation failed
    */
  def onFailedValidation(instance: Any,
                         failures: List[ValidationFailure],
                         valuesMap: Option[Map[String, Seq[String]]],
                         json: Option[Json],
                         bindingMetadata: Map[String, Any]): Unit


}
