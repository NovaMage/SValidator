package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass, BindingResult}
import com.github.novamage.svalidator.validation.binding.BindingLocalizer

class LongBinder(config: BindingConfig) extends TypedBinder[Long] {

  def bind(fieldName: String, valueMap: Map[String, Seq[String]], localizer: BindingLocalizer): BindingResult[Long] = {
    try {
      BindingPass(valueMap(fieldName).headOption.map(_.trim).filterNot(_.isEmpty).map(_.toLong).get)
    } catch {
      case ex: NumberFormatException => new BindingFailure(fieldName, config.languageConfig.invalidLongMessage(fieldName, valueMap.get(fieldName).flatMap(_.headOption).getOrElse(""), localizer), Some(ex))
      case ex: NoSuchElementException => new BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName, localizer), Some(ex))
    }
  }
}
