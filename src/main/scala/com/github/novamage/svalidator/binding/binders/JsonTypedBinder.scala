package com.github.novamage.svalidator.binding.binders

import com.github.novamage.svalidator.binding.BindingResult
import io.circe.ACursor


/** Performs binding of a string value taken from a parsed json using a field name to convert it to a typed value of the
  * target type
  *
  * @tparam A Type of the resulting bound value
  */
trait JsonTypedBinder[A] {


  /** Binds the value in the parsed json with the given field name to the type parameter  of this
    * [[com.github.novamage.svalidator.binding.binders.JsonTypedBinder JsonTypedBinder]]
    *
    * @param currentCursor   A json tree cursor pointing to the current field being bound
    * @param fieldName       Name of the field to bind
    * @param bindingMetadata Additional values passed as metadata for binding
    * @return BindingPass with the bound value if successful, BindingFailure with errors and throwable cause otherwise
    */
  def bindJson(currentCursor: ACursor,
               fieldName: Option[String],
               bindingMetadata: Map[String, Any]): BindingResult[A]
}
