package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.ITypedBinder
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass, BindingResult}

class LongBinder(config: BindingConfig) extends ITypedBinder[Long] {

  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[Long] = {
    try {
      BindingPass(valueMap(fieldName).head.toLong)
    } catch {
      case ex: NumberFormatException => new BindingFailure(fieldName, config.languageConfig.invalidLongMessage(fieldName, valueMap(fieldName).head), Some(ex))
      case ex: NoSuchElementException => new BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName), Some(ex))
    }
  }
}
