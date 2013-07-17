package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass, BindingResult}
import com.github.novamage.svalidator.binding.binders.ITypedBinder

class IntBinder(config: BindingConfig) extends ITypedBinder[Int] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[Int] = {
    try {
      BindingPass(valueMap(fieldName).head.toInt)
    } catch {
      case ex: NumberFormatException => new BindingFailure(fieldName, config.languageConfig.invalidIntegerMessage(fieldName, valueMap(fieldName).head.toString), Some(ex))
      case ex: NoSuchElementException => new BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName), Some(ex))
    }
  }
}
