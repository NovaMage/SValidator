package com.github.novamage.svalidator.binding

import com.github.novamage.svalidator.validation.Localizer

trait BindingLanguageConfig {

  def noValueProvidedMessage(fieldName: String, localizer: Localizer): String

  def invalidNonEmptyTextMessage(fieldName: String, localizer: Localizer): String

  def invalidBooleanMessage(fieldName: String, fieldValue: String, localizer: Localizer): String

  def invalidIntegerMessage(fieldName: String, fieldValue: String, localizer: Localizer): String

  def invalidLongMessage(fieldName: String, fieldValue: String, localizer: Localizer): String

  def invalidFloatMessage(fieldName: String, fieldValue: String, localizer: Localizer): String

  def invalidDoubleMessage(fieldName: String, fieldValue: String, localizer: Localizer): String

  def invalidDecimalMessage(fieldName: String, fieldValue: String, localizer: Localizer): String

  def invalidTimestampMessage(fieldName: String, fieldValue: String, localizer: Localizer): String

  def invalidEnumerationMessage(fieldName: String, localizer: Localizer): String

}

