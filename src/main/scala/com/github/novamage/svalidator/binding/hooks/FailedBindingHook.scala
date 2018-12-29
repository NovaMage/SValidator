package com.github.novamage.svalidator.binding.hooks

import com.github.novamage.svalidator.binding.FieldError

import scala.reflect.runtime.{universe => ru}

/** Trait for hooking behavior to calls of
  * [[com.github.novamage.svalidator.validation.binding.MappingBindingValidatorWithData#bindAndValidate MappingBindingValidatorWithData.bindAndValidate]]
  * and its subclasses after the binding step, and binding has failed.
  */
trait FailedBindingHook {

  /**
    *
    * @param errors          Field errors that occurred during binding
    * @param valuesMap       Values map passed to the binding process
    * @param bindingMetadata Binding metadata for current binding process
    * @param tag             Type tag of the type being bound
    */
  def onFailedBind[A](errors: List[FieldError],
                      valuesMap: Map[String, Seq[String]],
                      bindingMetadata: Map[String, Any])(implicit tag: ru.TypeTag[A]): Unit


}
