package com.github.novamage.svalidator.validation

/** Provides functionality for localizing error message keys in validation classes.
  */
trait Localizer {

  /** Returns the localized message for the given message key
    *
    * @param messageKey Message key to localize
    */
  def localize(messageKey: String): String

}
