package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding.binders.ITypedBinder
import scala.reflect.runtime.{universe => ru}
import com.github.novamage.svalidator.binding.{BindingFailure, BindingPass}

class EnumerationBinder(runtimeType: ru.Type, mirror: ru.Mirror) extends ITypedBinder[Any] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]]) = {
    val enumType = runtimeType.asInstanceOf[ru.TypeRef].pre
    val companionModuleSymbol = enumType.typeSymbol.asClass.companionSymbol.asModule
    val applySymbol = enumType.member(ru.newTermName("apply")).asMethod
    val reflectedCompanion = mirror.reflectModule(companionModuleSymbol)
    val instanceMirror = mirror.reflect(reflectedCompanion.instance)
    val applyMethod = instanceMirror.reflectMethod(applySymbol)
    try {
      BindingPass(applyMethod(valueMap(fieldName).head.toInt))
    } catch {
      case ex: NoSuchElementException => new BindingFailure(fieldName, "Invalid enumeration value", Some(ex))
      case ex: NumberFormatException => new BindingFailure(fieldName, "Invalid enumeration value", Some(ex))
    }
  }
}
