package com.github.novamage.svalidator.binding

trait IBindingLanguageConfig {

  def noValueProvidedMessage(fieldName: String): String

  def invalidNonEmptyTextMessage(fieldName: String): String

  def invalidBooleanMessage(fieldName: String, fieldValue: String): String

  def invalidIntegerMessage(fieldName: String, fieldValue: String): String

  def invalidLongMessage(fieldName: String, fieldValue: String): String

  def invalidFloatMessage(fieldName: String, fieldValue: String): String

  def invalidDoubleMessage(fieldName: String, fieldValue: String): String

  def invalidDecimalMessage(fieldName: String, fieldValue: String): String

  def invalidTimestampMessage(fieldName: String, fieldValue: String): String

  def invalidEnumerationMessage(fieldName: String): String

}

