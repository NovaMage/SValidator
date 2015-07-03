package com.github.novamage.svalidator.binding.binders.special

import java.lang.reflect.InvocationTargetException

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass}

import scala.reflect.runtime.{universe => ru}

class EnumerationBinder(runtimeType: ru.Type, mirror: ru.Mirror, config: BindingConfig) extends TypedBinder[Any] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]]) = {
    val enumType = runtimeType.asInstanceOf[ru.TypeRef].pre
    val companionModuleSymbol = enumType.typeSymbol.asClass.companionSymbol.asModule
    val applySymbol = enumType.member(ru.newTermName("apply")).asMethod
    val reflectedCompanion = mirror.reflectModule(companionModuleSymbol)
    val instanceMirror = mirror.reflect(reflectedCompanion.instance)
    val applyMethod = instanceMirror.reflectMethod(applySymbol)
    try {
      BindingPass(applyMethod(valueMap(fieldName).headOption.map(_.trim).filterNot(_.isEmpty).map(_.toInt).get))
    } catch {
      case ex: InvocationTargetException => new BindingFailure(fieldName, config.languageConfig.invalidEnumerationMessage(fieldName), Some(ex))
      case ex: NumberFormatException => new BindingFailure(fieldName, config.languageConfig.invalidEnumerationMessage(fieldName), Some(ex))
      case ex: NoSuchElementException => new BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName), Some(ex))
    }
  }
}
