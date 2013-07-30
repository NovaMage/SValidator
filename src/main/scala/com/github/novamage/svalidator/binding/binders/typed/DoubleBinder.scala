package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.ITypedBinder
import com.github.novamage.svalidator.binding.{BindingFailure, BindingPass, BindingConfig, BindingResult}

class DoubleBinder(config: BindingConfig) extends ITypedBinder[Double] {

  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[Double] = {
    try {
      BindingPass(valueMap(fieldName).headOption.map(_.trim).filterNot(_.isEmpty).map(_.toDouble).get)
    } catch {
      case ex: NumberFormatException => new BindingFailure(fieldName, config.languageConfig.invalidDoubleMessage(fieldName, valueMap(fieldName).head.toString), Some(ex))
      case ex: NoSuchElementException => new BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName), Some(ex))
    }
  }
}
