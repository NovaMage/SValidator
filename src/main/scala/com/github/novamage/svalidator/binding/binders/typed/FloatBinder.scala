package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass, BindingResult}

/** Performs binding of a float field
  *
  * @param config The configuration to use for error messages
  */
class FloatBinder(config: BindingConfig) extends TypedBinder[Float] {

  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[Float] = {
    try {
      BindingPass(valueMap(fieldName).headOption.map(_.trim).filterNot(_.isEmpty).map(_.toFloat).get)
    } catch {
      case ex: NumberFormatException => BindingFailure(fieldName, config.languageConfig.invalidFloatMessage(fieldName, valueMap.get(fieldName).flatMap(_.headOption).getOrElse("")), Some(ex))
      case ex: NoSuchElementException => BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName), Some(ex))
    }
  }

}
