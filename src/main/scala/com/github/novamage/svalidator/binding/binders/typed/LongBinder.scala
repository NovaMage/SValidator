package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass, BindingResult}

/** Performs binding of a long field
  *
  * @param config The configuration to use for error messages
  */
class LongBinder(config: BindingConfig) extends TypedBinder[Long] {

  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[Long] = {
    try {
      BindingPass(valueMap(fieldName).headOption.map(_.trim).filterNot(_.isEmpty).map(_.toLong).get)
    } catch {
      case ex: NumberFormatException => BindingFailure(fieldName, config.languageConfig.invalidLongMessage(fieldName, valueMap.get(fieldName).flatMap(_.headOption).getOrElse("")), Some(ex))
      case ex: NoSuchElementException => BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName), Some(ex))
    }
  }
}
