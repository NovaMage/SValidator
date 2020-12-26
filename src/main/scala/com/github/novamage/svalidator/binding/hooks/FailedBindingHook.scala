package com.github.novamage.svalidator.binding.hooks

import com.github.novamage.svalidator.binding.FieldError
import io.circe.Json

import scala.reflect.runtime.{universe => ru}

/** Trait for hooking behavior to calls of
  * [[com.github.novamage.svalidator.validation.binding.MappingBindingValidatorWithData#bindAndValidate MappingBindingValidatorWithData.bindAndValidate]]
  * and its subclasses after the binding step, and binding has failed.
  */
trait FailedBindingHook {

  /**
    *
    * @param errors          Field errors that occurred during binding
    * @param valuesMap       Values map passed to the binding process, or None if json was used during binding
    * @param json            json passed to the binding process, or None if values map was used during binding
    * @param bindingMetadata Binding metadata for current binding process
    * @param tag             Type tag of the type being bound
    */
  def onFailedBind[A](errors: List[FieldError],
                      valuesMap: Option[Map[String, Seq[String]]],
                      json: Option[Json],
                      bindingMetadata: Map[String, Any])(implicit tag: ru.TypeTag[A]): Unit


}
