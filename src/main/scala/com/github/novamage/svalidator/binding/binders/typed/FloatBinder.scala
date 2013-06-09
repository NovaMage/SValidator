package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.ITypedBinder
import com.github.novamage.svalidator.binding.{BindingFailure, BindingPass, BindingConfig, BindingResult}

class FloatBinder(config: BindingConfig) extends ITypedBinder[Float] {

  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[Float] = {
    try {
      BindingPass(valueMap(fieldName).head.toFloat)
    } catch {
      case ex: NumberFormatException => new BindingFailure(fieldName, config.languageConfig.invalidFloatMessage(fieldName, valueMap(fieldName).head.toString))
      case ex: NoSuchElementException => new BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName))
    }
  }

}
