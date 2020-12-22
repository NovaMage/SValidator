package com.github.novamage.svalidator.binding

import com.github.novamage.svalidator.validation.MessageParts

/** Default implementation for [[com.github.novamage.svalidator.binding.BindingLanguageConfig BindingLanguageConfig]]
  *
  */
object DefaultBindingLanguageConfig extends BindingLanguageConfig {

  def noValueProvidedMessage(fieldName: String): MessageParts = {
    MessageParts("required.field", List(fieldName))
  }

  def invalidNonEmptyTextMessage(fieldName: String): MessageParts = {
    MessageParts("required.field", List(fieldName))
  }

  def invalidBooleanMessage(fieldName: String,
                            fieldValue: String): MessageParts = {
    MessageParts("invalid.boolean", List(fieldValue))
  }

  def invalidIntegerMessage(fieldName: String,
                            fieldValue: String): MessageParts = {
    MessageParts("invalid.integer", List(fieldValue))
  }

  def invalidLongMessage(fieldName: String,
                         fieldValue: String): MessageParts = {
    MessageParts("invalid.long", List(fieldValue))
  }

  def invalidFloatMessage(fieldName: String,
                          fieldValue: String): MessageParts = {
    MessageParts("invalid.float", List(fieldValue))
  }

  def invalidDoubleMessage(fieldName: String,
                           fieldValue: String): MessageParts = {
    MessageParts("invalid.double", List(fieldValue))
  }

  def invalidDecimalMessage(fieldName: String,
                            fieldValue: String): MessageParts = {
    MessageParts("invalid.decimal", List(fieldValue))
  }

  def invalidTimestampMessage(fieldName: String,
                              fieldValue: String): MessageParts = {
    MessageParts("invalid.timestamp", List(fieldValue))
  }

  def invalidEnumerationMessage(fieldName: String,
                                fieldValue: String): MessageParts = {
    MessageParts("invalid.enumeration", List(fieldValue))
  }

  override def invalidSequenceMessage(fieldName: String, fieldValue: String): MessageParts = {
    MessageParts("invalid.sequence", List(fieldValue))
  }

  override def invalidJsonMessage(fieldName: Option[String], fieldValue: String): MessageParts = {
    MessageParts("invalid.json", List(fieldValue))
  }
}

