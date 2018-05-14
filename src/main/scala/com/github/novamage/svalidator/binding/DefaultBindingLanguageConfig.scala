package com.github.novamage.svalidator.binding

import com.github.novamage.svalidator.validation.Localizer

object DefaultBindingLanguageConfig extends BindingLanguageConfig {

  def noValueProvidedMessage(fieldName: String,
                             localizer: Localizer): String = {
    localizer.localize("This field is required")
  }

  def invalidNonEmptyTextMessage(fieldName: String,
                                 localizer: Localizer): String = {
    localizer.localize("This field must be a valid non-empty text")
  }

  def invalidBooleanMessage(fieldName: String,
                            fieldValue: String,
                            localizer: Localizer): String = {
    localizer.localize("The value '%s' is not a valid boolean.").format(fieldValue)
  }

  def invalidIntegerMessage(fieldName: String,
                            fieldValue: String,
                            localizer: Localizer): String = {
    localizer.localize("The value '%s' is not a valid integer").format(fieldValue)
  }

  def invalidLongMessage(fieldName: String,
                         fieldValue: String,
                         localizer: Localizer): String = {
    localizer.localize("The value '%s' is not a valid long").format(fieldValue)
  }

  def invalidFloatMessage(fieldName: String,
                          fieldValue: String,
                          localizer: Localizer): String = {
    localizer.localize("The value '%s' is not a valid float").format(fieldValue)
  }

  def invalidDoubleMessage(fieldName: String,
                           fieldValue: String,
                           localizer: Localizer): String = {
    localizer.localize("The value '%s' is not a valid double").format(fieldValue)
  }

  def invalidDecimalMessage(fieldName: String,
                            fieldValue: String,
                            localizer: Localizer): String = {
    localizer.localize("The value '%s' is not a valid decimal").format(fieldValue)
  }

  def invalidTimestampMessage(fieldName: String,
                              fieldValue: String,
                              localizer: Localizer): String = {
    localizer.localize("The value '%s' is not a valid date").format(fieldValue)
  }

  def invalidEnumerationMessage(fieldName: String,
                                localizer: Localizer): String = {
    localizer.localize("The value provided for '%s' is not valid").format(fieldName)
  }
}

