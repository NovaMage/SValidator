package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.{BindingFailure, BindingPass, BindingResult}
import com.github.novamage.svalidator.binding.binders.ITypeBinder

class BooleanBinder extends ITypeBinder[Boolean] {

  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[Boolean] = {
    try {
      BindingPass(valueMap(fieldName).head.toBoolean)
    } catch {
      case ex: NumberFormatException => new BindingFailure(fieldName, s"The value '${valueMap(fieldName)}' is not a valid boolean.")
      case ex: NoSuchElementException => new BindingFailure(fieldName, s"The field '$fieldName' does not contain any values within the given map.")
    }
  }
}
