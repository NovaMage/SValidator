package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.{BindingFailure, BindingConfig, BindingPass, BindingResult}
import com.github.novamage.svalidator.binding.binders.ITypedBinder

class StringBinder(config: BindingConfig) extends ITypedBinder[String] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[String] = {
    try {
      BindingPass(valueMap(fieldName).headOption.map(_.trim).filterNot(_.isEmpty).get)
    } catch {
      case ex: NoSuchElementException => new BindingFailure(fieldName, config.languageConfig.invalidNonEmptyTextMessage(fieldName), Some(ex))
    }
  }
}
