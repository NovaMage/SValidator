package com.github.novamage.svalidator.binding

import com.github.novamage.svalidator.validation.binding.BindingLocalizer

trait BindingLanguageConfig {

  def noValueProvidedMessage(fieldName: String, localizer:BindingLocalizer): String

  def invalidNonEmptyTextMessage(fieldName: String, localizer:BindingLocalizer): String

  def invalidBooleanMessage(fieldName: String, fieldValue: String, localizer:BindingLocalizer): String

  def invalidIntegerMessage(fieldName: String, fieldValue: String, localizer:BindingLocalizer): String

  def invalidLongMessage(fieldName: String, fieldValue: String, localizer:BindingLocalizer): String

  def invalidFloatMessage(fieldName: String, fieldValue: String, localizer:BindingLocalizer): String

  def invalidDoubleMessage(fieldName: String, fieldValue: String, localizer:BindingLocalizer): String

  def invalidDecimalMessage(fieldName: String, fieldValue: String, localizer:BindingLocalizer): String

  def invalidTimestampMessage(fieldName: String, fieldValue: String, localizer:BindingLocalizer): String

  def invalidEnumerationMessage(fieldName: String, localizer:BindingLocalizer): String

}

