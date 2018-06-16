package com.github.novamage.svalidator.binding

import com.github.novamage.svalidator.validation.{Localizer, MessageParts}

/** Contains information of errors for fields during binding
  *
  * @param fieldName Name of the field that caused the error
  * @param messageParts Error message information for the field
  */
sealed case class FieldError(fieldName: String, messageParts: MessageParts) {

  /** Returns the message key of this object's [[com.github.novamage.svalidator.validation.MessageParts MessageParts]]
    * formatted alongside the format values using [[scala.collection.immutable.StringLike#format StringLike.format]]'s method.
    */
  def message: String = messageParts.messageKey.format(messageParts.messageFormatValues: _*)

  /** Returns a field error with localized messageParts generated passing the given localizer to
    * [[com.github.novamage.svalidator.validation.MessageParts#localize MessageParts.localize]]'s method.
    *
    * @param localizer Localizer to apply to the [[com.github.novamage.svalidator.validation.MessageParts MessageParts]]
    */
  def localize(implicit localizer: Localizer): FieldError = {
    FieldError(fieldName, messageParts.localize)
  }

}

/** Contains helper methods for alternate ways of building a [[com.github.novamage.svalidator.binding.FieldError FieldError]]
  */
object FieldError {

  /** Returns a field error with the given field name and message without formatting arguments
    */
  def apply(fieldName: String, message: String): FieldError = FieldError(fieldName, MessageParts(message))

}
