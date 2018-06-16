package com.github.novamage.svalidator.validation.binding

import scala.reflect.runtime.{universe => ru}

/** Base class that provides binding alongside validation of a
  * [[com.github.novamage.svalidator.validation.simple.SimpleValidator SimpleValidator]]
  *
  * @tparam A Type of the instance that will be bound and validated
  */
abstract class BindingValidator[A: ru.TypeTag] extends MappingBindingValidator[A] {

  /** Attempts to perform binding and validation of the given type using the specified values map.
    *
    * This method calls
    * [[com.github.novamage.svalidator.binding.binders.special.MapToObjectBinder#bind MapToObjectBinder.bind]] method to
    * perform the binding, and, if successful, calls <code>validate</code> on it.  If not, field errors are converted
    * to validation failures and returned in the summary
    *
    * @param valuesMap Values map to use for binding
    * @tparam B Type of the instance being bound and validated
    * @return A summary of field errors or validation failures if any ocurred, or a summary containing the bound instance
    *         otherwise.
    */
  def bindAndValidate[B](valuesMap: Map[String, Seq[String]])(implicit tag: ru.TypeTag[B]): BindingAndValidationSummary[A] = {
    super.bindAndValidate(valuesMap, (x: A) => x)
  }


}
