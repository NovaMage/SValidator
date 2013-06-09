package com.github.novamage.svalidator.binding

object DefaultBindingLanguageConfig extends BindingLanguageConfig {

  def noValueProvidedMessage(fieldName: String) = s"This field is required"

  def invalidNonEmptyTextMessage(fieldName: String) = s"This field must be a valid non-empty text"

  def invalidBooleanMessage(fieldName: String, fieldValue: String) = s"The value '$fieldValue' is not a valid boolean."

  def invalidIntegerMessage(fieldName: String, fieldValue: String) = s"The value '$fieldValue' is not a valid integer"

  def invalidLongMessage(fieldName: String, fieldValue: String) = s"The value '$fieldValue' is not a valid long"

  def invalidFloatMessage(fieldName: String, fieldValue: String) = s"The value '$fieldValue' is not a valid float"

  def invalidDoubleMessage(fieldName: String, fieldValue: String) = s"The value '$fieldValue' is not a valid double"

  def invalidDecimalMessage(fieldName: String, fieldValue: String) = s"The value '$fieldValue' is not a valid decimal"

  def invalidTimestampMessage(fieldName: String, fieldValue: String) = s"The value '$fieldValue' is not a valid timestamp"

}

