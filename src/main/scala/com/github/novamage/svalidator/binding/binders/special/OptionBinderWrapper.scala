package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding.binders.ITypedBinder
import com.github.novamage.svalidator.binding.{BindingFailure, BindingPass, BindingResult}

class OptionBinderWrapper(wrappedBinder: ITypedBinder[_]) extends ITypedBinder[Option[Any]] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[Option[Any]] = {
    wrappedBinder.bind(fieldName, valueMap) match {
      case BindingPass(value) => BindingPass(Option(value))
      case BindingFailure(errors, cause) => cause match {
        case Some(x) if x.isInstanceOf[NoSuchElementException] => BindingPass(None)
        case _ => BindingFailure(errors, cause)
      }
    }
  }
}
