package com.github.novamage.svalidator.binding

class IntBinder extends ITypeBinder[Int] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[Int] = {
    try {
      BindingResult(List(), Some(valueMap(fieldName).head.toInt))
    } catch {
      case ex: NumberFormatException => BindingResult(List(s"The value '${valueMap(fieldName)}' is not a valid integer."), None)
      case ex: NoSuchElementException => BindingResult(List(s"The field '$fieldName' is does not contain any values within the given map."), None)
    }
  }
}
