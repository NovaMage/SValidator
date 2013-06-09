package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding.binders.ITypedBinder
import scala.reflect.runtime.{universe => ru}
import com.github.novamage.svalidator.binding.BindingResult

class RecursiveBinderWrapper[A: ru.TypeTag] extends ITypedBinder[A] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[A] = {
    MapToObjectBinder.bind[A](Some(fieldName), valueMap)
  }
}
