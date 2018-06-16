package com.github.novamage.svalidator.binding.binders

import com.github.novamage.svalidator.binding.BindingResult

/** Performs binding of a string value taken from a values map using a field name to convert it to a typed value of the
  * target type
  *
  * @tparam A Type of the resulting bound value
  */
trait TypedBinder[A] {

  /** Binds the value in the values map with the given field name to the type parameter  of this
    * [[com.github.novamage.svalidator.binding.binders.TypedBinder TypedBinder]]
    *
    * @param fieldName Name of the field to bind
    * @param valueMap Map of values to provide for the binding
    * @return BindingPass with the bound value if successful, BindingFailure with errors and throwable cause otherwise
    */
  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[A]
}
