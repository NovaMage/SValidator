package com.github.novamage.svalidator.binding

/** Specifies date format and binding error messages for default binders
  *
  * @param dateFormat Format of dates to be expected during binding
  * @param languageConfig Configuration of error messages for default binders
  */
class BindingConfig(val dateFormat: String, val languageConfig: BindingLanguageConfig) {
}

object BindingConfig {

  /** Returns the default implementation for binding configuration
    *
    */
  lazy val defaultConfig = BindingConfig("yyyy-MM-dd", DefaultBindingLanguageConfig)

  def apply(dateFormat: String, languageConfig: BindingLanguageConfig) = new BindingConfig(dateFormat, languageConfig)
}


