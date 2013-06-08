package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.{BindingFailure, BindingPass, BindingResult}
import com.github.novamage.svalidator.binding.binders.ITypeBinder

class IntBinder extends ITypeBinder[Int] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[Int] = {
    try {
      BindingPass(valueMap(fieldName).head.toInt)
    } catch {
      case ex: NumberFormatException => new BindingFailure(fieldName, s"The value '${valueMap(fieldName)}' is not a valid integer.")
      case ex: NoSuchElementException => new BindingFailure(fieldName, s"The field '$fieldName' is does not contain any values within the given map.")
    }
  }
}
