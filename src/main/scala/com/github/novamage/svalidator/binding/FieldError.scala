package com.github.novamage.svalidator.binding

import com.github.novamage.svalidator.validation.{Localizer, MessageParts}

sealed case class FieldError(fieldName: String, messageParts: MessageParts) {

  def message: String = messageParts.messageKey.format(messageParts.messageFormatValues: _*)

  def localize(implicit localizer: Localizer): FieldError = {
    FieldError(fieldName, messageParts.localize)
  }

}

object FieldError {

  def apply(fieldName: String, message: String): FieldError = FieldError(fieldName, MessageParts(message))

}
