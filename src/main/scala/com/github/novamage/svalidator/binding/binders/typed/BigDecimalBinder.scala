package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.ITypedBinder
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass}

class BigDecimalBinder(config: BindingConfig) extends ITypedBinder[BigDecimal] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]]) = {

    try {
      BindingPass(BigDecimal(valueMap(fieldName).head))
    } catch {
      case ex: NumberFormatException => new BindingFailure(fieldName, config.languageConfig.invalidDecimalMessage(fieldName, valueMap(fieldName).head.toString))
      case ex: NoSuchElementException => new BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName))
    }
  }
}
