package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass}
import com.github.novamage.svalidator.validation.binding.BindingLocalizer

class BigDecimalBinder(config: BindingConfig) extends TypedBinder[BigDecimal] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]], localizer: BindingLocalizer) = {

    try {
      BindingPass(BigDecimal(valueMap(fieldName).headOption.map(_.trim).filterNot(_.isEmpty).get))
    } catch {
      case ex: NumberFormatException => new BindingFailure(fieldName, config.languageConfig.invalidDecimalMessage(fieldName, valueMap.get(fieldName).flatMap(_.headOption).getOrElse(""), localizer), Some(ex))
      case ex: NoSuchElementException => new BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName, localizer), Some(ex))
    }
  }
}
