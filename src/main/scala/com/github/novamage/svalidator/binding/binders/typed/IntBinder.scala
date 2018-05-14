package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass, BindingResult}
import com.github.novamage.svalidator.validation.Localizer

class IntBinder(config: BindingConfig) extends TypedBinder[Int] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]], localizer: Localizer): BindingResult[Int] = {
    try {
      BindingPass(valueMap(fieldName).headOption.map(_.trim).filterNot(_.isEmpty).map(_.toInt).get)
    } catch {
      case ex: NumberFormatException => new BindingFailure(fieldName, config.languageConfig.invalidIntegerMessage(fieldName, valueMap.get(fieldName).flatMap(_.headOption).getOrElse(""), localizer), Some(ex))
      case ex: NoSuchElementException => new BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName, localizer), Some(ex))
    }
  }
}
