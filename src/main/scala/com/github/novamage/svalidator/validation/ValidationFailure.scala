package com.github.novamage.svalidator.validation

/** Contains information about a single error that occurred during validation
  *
  * @param fieldName Name of the field that caused the error
  * @param messageParts The error message string and any format values
  * @param metadata Additional metadata specific to the error that occurred (i.e. Error codes)
  */
case class ValidationFailure(fieldName: String,
                             messageParts: MessageParts,
                             metadata: Map[String, List[Any]]) {


  /** Returns the message key of this object's [[com.github.novamage.svalidator.validation.MessageParts MessageParts]]
    * formatted alongside the format values using [[scala.collection.immutable.StringLike#format StringLike.format]]'s method.
    */
  def message: String = messageParts.message

  /** Returns a failure with localized messageParts generated passing the given localizer to
    * [[com.github.novamage.svalidator.validation.MessageParts#localize MessageParts.localize]]'s method.
    *
    * @param localizer Localizer to apply to the [[com.github.novamage.svalidator.validation.MessageParts MessageParts]]
    */
  def localize(implicit localizer: Localizer): ValidationFailure = {
    ValidationFailure(fieldName, messageParts.localize, metadata)
  }

}

object ValidationFailure {

  /** Helper to generate a failure with a simple message without arguments
    *
    * @param fieldName Name of the field that caused the error
    * @param message Message key of the error
    * @return A validation failure with the given field name and message key
    */
  def apply(fieldName: String, message: String): ValidationFailure = {
    ValidationFailure(fieldName, MessageParts(message), Map.empty)
  }

}
