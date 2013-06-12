package com.github.novamage.svalidator.binding

class BindingConfig(val dateFormat: String, val languageConfig: IBindingLanguageConfig) {
}

object BindingConfig {

  lazy val defaultConfig = BindingConfig("yyyy-MM-dd", DefaultBindingLanguageConfig)

  def apply(dateFormat: String, languageConfig: IBindingLanguageConfig) = new BindingConfig(dateFormat, languageConfig)
}


