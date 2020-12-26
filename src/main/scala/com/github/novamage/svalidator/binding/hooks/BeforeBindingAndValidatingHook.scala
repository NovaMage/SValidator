package com.github.novamage.svalidator.binding.hooks

import io.circe.Json

import scala.reflect.runtime.{universe => ru}

/** Trait for hooking behavior to calls of
  * [[com.github.novamage.svalidator.validation.binding.MappingBindingValidatorWithData#bindAndValidate MappingBindingValidatorWithData.bindAndValidate]]
  * and its subclasses before the binding step.
  */
trait BeforeBindingAndValidatingHook {

  /**
    * @param valuesMap       Values map passed to the binding process, or None if json was used during binding
    * @param json            json passed to the binding process, or None if values map was used during binding
    * @param bindingMetadata Binding metadata for current binding process
    * @param tag             Type tag of the type being bound
    */
  def beforeBind[C](valuesMap: Option[Map[String, Seq[String]]],
                    json: Option[Json],
                    bindingMetadata: Map[String, Any])(implicit tag: ru.TypeTag[C]): Unit

}

