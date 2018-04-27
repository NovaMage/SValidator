package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding.BindingResult
import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.validation.binding.BindingLocalizer

import scala.reflect.runtime.{universe => ru}

class RecursiveBinder[A: ru.TypeTag] extends TypedBinder[A] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]], localizer: BindingLocalizer): BindingResult[A] = {
    MapToObjectBinder.bind[A](Some(fieldName), valueMap, localizer)
  }
}
