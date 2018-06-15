package com.github.novamage.svalidator.validation

/** Contains an error message and any values to be formatted with it
  *
  * @param messageKey Raw error message or a key string to be used in localization files
  * @param messageFormatValues Arguments to interpolate into the string.
  */
case class MessageParts(messageKey: String,
                        messageFormatValues: List[Any] = Nil) {


  /** Returns the message key of this object formatted alongside the format values using
    * [[scala.collection.immutable.StringLike#format StringLike.format]]'s method.
    */
  def message: String = messageKey.format(messageFormatValues: _*)

  /** Returns new parts whose messageKey is generated applying the localizer to the current message key.
    *
    * Message format values are not altered in any way.
    *
    * @param localizer Localizer to apply to the messageKey
    */
  def localize(implicit localizer: Localizer): MessageParts = {
    MessageParts(localizer.localize(messageKey), messageFormatValues)
  }
}
