package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding.binders.ITypeBinder
import com.github.novamage.svalidator.binding.{BindingPass, BindingResult}

class ListBinderWrapper(wrappedBinder: ITypeBinder[_]) extends ITypeBinder[List[Any]] {

  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[List[Any]] = {
    val valueList = (valueMap(fieldName) map {
      value => wrappedBinder.bind(fieldName, Map(fieldName -> List(value)))
    } collect {
      case BindingPass(value) => value
    }).toList

    BindingPass(valueList)
  }
}
