package com.github.novamage.svalidator.binding

trait BindingLanguageConfig {

  def noValueProvidedMessage(fieldName: String, localizationFunction: String => String): String

  def invalidNonEmptyTextMessage(fieldName: String, localizationFunction: String => String): String

  def invalidBooleanMessage(fieldName: String, fieldValue: String, localizationFunction: String => String): String

  def invalidIntegerMessage(fieldName: String, fieldValue: String, localizationFunction: String => String): String

  def invalidLongMessage(fieldName: String, fieldValue: String, localizationFunction: String => String): String

  def invalidFloatMessage(fieldName: String, fieldValue: String, localizationFunction: String => String): String

  def invalidDoubleMessage(fieldName: String, fieldValue: String, localizationFunction: String => String): String

  def invalidDecimalMessage(fieldName: String, fieldValue: String, localizationFunction: String => String): String

  def invalidTimestampMessage(fieldName: String, fieldValue: String, localizationFunction: String => String): String

  def invalidEnumerationMessage(fieldName: String, localizationFunction: String => String): String

}

