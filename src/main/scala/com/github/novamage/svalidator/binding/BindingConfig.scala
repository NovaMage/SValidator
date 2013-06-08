package com.github.novamage.svalidator.binding

class BindingConfig(val dateFormat: String, val languageConfig: BindingLanguageConfig) {
}

object BindingConfig {

  lazy val defaultConfig = BindingConfig("yyyy-MM-dd", DefaultBindingLanguageConfig)

  def apply(dateFormat: String, languageConfig: BindingLanguageConfig) = new BindingConfig(dateFormat, languageConfig)
}

