package com.github.novamage.svalidator.binding

class BooleanBinder extends ITypeBinder[Boolean] {

  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[Boolean] = {
    try {
      BindingResult(List(), Some(valueMap(fieldName).head.toBoolean))
    } catch {
      case ex: NumberFormatException => BindingResult(List(s"The value '${valueMap(fieldName)}' is not a valid boolean."), None)
      case ex: NoSuchElementException => BindingResult(List(s"The field '$fieldName' is does not contain any values within the given map."), None)
    }
  }
}
