package com.github.novamage.svalidator.binding

object DefaultBindingLanguageConfig extends BindingLanguageConfig {

  def noValueProvidedMessage(fieldName: String) = s"The field '$fieldName' does not contain any values within the given map"

  def invalidNonEmptyTextMessage(fieldName: String) = s"The field '$fieldName' is not a valid non-empty string"

  def invalidBooleanMessage(fieldName: String, fieldValue: String) = s"The value '$fieldValue' is not a valid boolean."

  def invalidIntegerMessage(fieldName: String, fieldValue: String) = s"The value '$fieldValue' is not a valid integer"

  def invalidLongMessage(fieldName: String, fieldValue: String) = s"The value '$fieldValue' is not a valid long"

  def invalidTimestampMessage(fieldName: String, fieldValue: String) = s"The value '$fieldValue' is not a valid timestamp"
}

