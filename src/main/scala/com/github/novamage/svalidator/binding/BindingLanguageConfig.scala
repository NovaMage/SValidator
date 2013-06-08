package com.github.novamage.svalidator.binding

trait BindingLanguageConfig {

  def noValueProvidedMessage(fieldName: String): String

  def invalidNonEmptyTextMessage(fieldName: String): String

  def invalidBooleanMessage(fieldName: String, fieldValue: String): String

  def invalidIntegerMessage(fieldName: String, fieldValue: String): String

  def invalidLongMessage(fieldName: String, fieldValue: String): String

  def invalidTimestampMessage(fieldName: String, fieldValue: String): String

}

