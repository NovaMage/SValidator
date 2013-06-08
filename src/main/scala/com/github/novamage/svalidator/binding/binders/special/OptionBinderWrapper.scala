package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding.binders.ITypeBinder
import com.github.novamage.svalidator.binding.{BindingFailure, BindingPass, BindingResult}

class OptionBinderWrapper(wrappedBinder: ITypeBinder[_]) extends ITypeBinder[Option[Any]] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[Option[Any]] = {
    wrappedBinder.bind(fieldName, valueMap) match {
      case BindingPass(value) => BindingPass(Option(value))
      case x: BindingFailure[_] => BindingPass(None)
    }
  }
}
