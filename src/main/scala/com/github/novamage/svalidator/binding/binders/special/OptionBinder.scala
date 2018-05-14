package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingFailure, BindingPass, BindingResult}
import com.github.novamage.svalidator.validation.Localizer

class OptionBinder(wrappedBinder: TypedBinder[_]) extends TypedBinder[Option[Any]] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]], localizer: Localizer): BindingResult[Option[Any]] = {
    wrappedBinder.bind(fieldName, valueMap, localizer) match {
      case BindingPass(value) => BindingPass(Option(value))
      case BindingFailure(errors, cause) => cause match {
        case Some(x) if x.isInstanceOf[NoSuchElementException] => BindingPass(None)
        case _ => BindingFailure(errors, cause)
      }
    }
  }
}
