package com.github.novamage.svalidator.binding

import com.github.novamage.svalidator.validation.MessageParts

/** Provides configuration for error messages to use in default binders when binding fails
  */
trait BindingLanguageConfig {

  /** Returns message parts to use when binding fails when no value was provided for a required input
    *
    * @param fieldName The name of the field being bound
    */
  def noValueProvidedMessage(fieldName: String): MessageParts

  /** Returns message parts to use when binding fails when no value was provided for a required text input
    *
    * @param fieldName The name of the field being bound
    */
  def invalidNonEmptyTextMessage(fieldName: String): MessageParts

  /** Returns message parts to use when binding fails for a boolean field
    *
    * @param fieldName The name of the field being bound
    * @param fieldValue The string that was attempted to be bound as a boolean
    */
  def invalidBooleanMessage(fieldName: String, fieldValue: String): MessageParts

  /** Returns message parts to use when binding fails for an integer field
    *
    * @param fieldName The name of the field being bound
    * @param fieldValue The string that was attempted to be bound as an integer
    */
  def invalidIntegerMessage(fieldName: String, fieldValue: String): MessageParts

  /** Returns message parts to use when binding fails for an integer field
    *
    * @param fieldName The name of the field being bound
    * @param fieldValue The string that was attempted to be bound as an integer
    */
  def invalidLongMessage(fieldName: String, fieldValue: String): MessageParts

  /** Returns message parts to use when binding fails for a float field
    *
    * @param fieldName The name of the field being bound
    * @param fieldValue The string that was attempted to be bound as a float
    */
  def invalidFloatMessage(fieldName: String, fieldValue: String): MessageParts

  /** Returns message parts to use when binding fails for a double field
    *
    * @param fieldName The name of the field being bound
    * @param fieldValue The string that was attempted to be bound as a double
    */
  def invalidDoubleMessage(fieldName: String, fieldValue: String): MessageParts

  /** Returns message parts to use when binding fails for a [[scala.BigDecimal BigDecimal]] field
    *
    * @param fieldName The name of the field being bound
    * @param fieldValue The string that was attempted to be bound as a [[scala.BigDecimal BigDecimal]]
    */
  def invalidDecimalMessage(fieldName: String, fieldValue: String): MessageParts

  /** Returns message parts to use when binding fails for a [[java.sql.Timestamp Timestamp]] field, according to the
    * date format provided in the configuration to the
    * [[com.github.novamage.svalidator.binding.TypeBinderRegistry#initializeBinders TypeBinderRegistry#initializeBinders(config)]]
    * method.
    *
    * @param fieldName The name of the field being bound
    * @param fieldValue The string that was attempted to be bound as a [[java.sql.Timestamp Timestamp]]
    */
  def invalidTimestampMessage(fieldName: String, fieldValue: String): MessageParts

  /** Returns message parts to use when binding fails for an object that follows the case-object enumeration pattern
    * described in [[https://github.com/NovaMage/SValidator/wiki/Type-Based-Enumerations Type Based Enumerations]].
    *
    * Essentially, assuming the enumeration is properly constructed, binding will fail if no enumeration value exists
    * that matches a given int id, if no value is found for the field, or if a value is found but is not a valid int.
    *
    * @param fieldName The name of the field being bound
    * @param fieldValue The string that was attempted to be bound as a value of a case-object enumeration
    */
  def invalidEnumerationMessage(fieldName: String, fieldValue: String): MessageParts

}

