package com.github.novamage.svalidator.binding.hooks

import scala.reflect.runtime.{universe => ru}

/** Trait for hooking behavior to calls of
  * [[com.github.novamage.svalidator.validation.binding.MappingBindingValidatorWithData#bindAndValidate MappingBindingValidatorWithData.bindAndValidate]]
  * and its subclasses before the binding step.
  */
trait BeforeBindingAndValidatingHook {

  /**
    *
    * @param valuesMap       Values map passed to the binding process
    * @param bindingMetadata Binding metadata for current binding process
    * @param tag             Type tag of the type being bound
    */
  def beforeBind[C](valuesMap: Map[String, Seq[String]],
                    bindingMetadata: Map[String, Any])(implicit tag: ru.TypeTag[C]): Unit

}

