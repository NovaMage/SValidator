package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass, BindingResult}
import com.github.novamage.svalidator.validation.Localizer

class BooleanBinder(config: BindingConfig) extends TypedBinder[Boolean] {

  def bind(fieldName: String, valueMap: Map[String, Seq[String]], localizer: Localizer): BindingResult[Boolean] = {
    try {
      BindingPass(valueMap.get(fieldName).exists(_.headOption.exists(_.toBoolean)))
    } catch {
      case ex: IllegalArgumentException => new BindingFailure(fieldName, config.languageConfig.invalidBooleanMessage(fieldName, valueMap.get(fieldName).flatMap(_.headOption).getOrElse(""), localizer), Some(ex))
    }
  }
}
