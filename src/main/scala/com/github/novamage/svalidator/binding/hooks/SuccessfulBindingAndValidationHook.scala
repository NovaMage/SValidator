package com.github.novamage.svalidator.binding.hooks

import io.circe.Json

/** Trait for hooking behavior to calls of
  * [[com.github.novamage.svalidator.validation.binding.MappingBindingValidatorWithData#bindAndValidate MappingBindingValidatorWithData.bindAndValidate]]
  * and its subclasses after both binding and validation were successful.
  */
trait SuccessfulBindingAndValidationHook {

  /**
    *
    * @param value           The value that was bound and validated
    * @param valuesMap       Values map used during binding of the value
    * @param bindingMetadata Metadata used during the binding of the value
    */
  def onSuccess(value: Any,
                valuesMap: Option[Map[String, Seq[String]]],
                json: Option[Json],
                bindingMetadata: Map[String, Any]): Unit

}
