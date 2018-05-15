package com.github.novamage.svalidator.validation

case class ValidationFailure(fieldName: String,
                             messageParts: MessageParts,
                             metadata: Map[String, List[Any]]) {


  def message: String = messageParts.messageKey.format(messageParts.messageFormatValues: _*)

  def localize(implicit localizer: Localizer): ValidationFailure = {
    ValidationFailure(fieldName, messageParts.localize, metadata)
  }

}

object ValidationFailure {

  def apply(fieldName: String, message: String): ValidationFailure = {
    ValidationFailure(fieldName, MessageParts(message), Map.empty)
  }

}
