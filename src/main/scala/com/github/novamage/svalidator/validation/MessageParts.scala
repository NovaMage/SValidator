package com.github.novamage.svalidator.validation

case class MessageParts(messageKey: String,
                        messageFormatValues: List[Any] = Nil) {


  def localize(implicit localizer: Localizer): MessageParts = {
    MessageParts(localizer.localize(messageKey), messageFormatValues)
  }
}
