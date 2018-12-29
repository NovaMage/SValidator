package com.github.novamage.svalidator.binding.hooks

import com.github.novamage.svalidator.validation.ValidationFailure

trait SuccessfulBindingFailedValidationHook {

  /**
    * Trait for hooking behavior to calls of
    * [[com.github.novamage.svalidator.validation.binding.MappingBindingValidatorWithData#bindAndValidate MappingBindingValidatorWithData.bindAndValidate]]
    * and its subclasses after binding was successful but validation failed
    */
  def onFailedValidation(instance: Any,
                         failures: List[ValidationFailure],
                         valuesMap: Map[String, Seq[String]],
                         bindingMetadata: Map[String, Any]): Unit


}
