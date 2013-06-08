package com.github.novamage.svalidator.binding

import com.github.novamage.svalidator.binding.binders.ITypeBinder
import scala.reflect.runtime.{universe => ru}

class RecursiveBinderWrapper[A: ru.TypeTag] extends ITypeBinder[A] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[A] = {
    MapToObjectBinder.bind[A](Some(fieldName), valueMap)
  }
}
