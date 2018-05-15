package com.github.novamage.svalidator.binding

import com.github.novamage.svalidator.validation.MessageParts

trait BindingLanguageConfig {

  def noValueProvidedMessage(fieldName: String): MessageParts

  def invalidNonEmptyTextMessage(fieldName: String): MessageParts

  def invalidBooleanMessage(fieldName: String, fieldValue: String): MessageParts

  def invalidIntegerMessage(fieldName: String, fieldValue: String): MessageParts

  def invalidLongMessage(fieldName: String, fieldValue: String): MessageParts

  def invalidFloatMessage(fieldName: String, fieldValue: String): MessageParts

  def invalidDoubleMessage(fieldName: String, fieldValue: String): MessageParts

  def invalidDecimalMessage(fieldName: String, fieldValue: String): MessageParts

  def invalidTimestampMessage(fieldName: String, fieldValue: String): MessageParts

  def invalidEnumerationMessage(fieldName: String, fieldValue: String): MessageParts

}

