package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding.BindingResult
import com.github.novamage.svalidator.binding.binders.TypedBinder

import scala.reflect.runtime.{universe => ru}

/** Reflectively binds a given type for which recursively binding into the type has been allowed
  */
class RecursiveBinder[A: ru.TypeTag] extends TypedBinder[A] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[A] = {
    MapToObjectBinder.bind[A](Some(fieldName), valueMap)
  }
}
