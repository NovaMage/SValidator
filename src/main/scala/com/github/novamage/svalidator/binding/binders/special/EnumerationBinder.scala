package com.github.novamage.svalidator.binding.binders.special

import java.lang.reflect.InvocationTargetException

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass, BindingResult}

import scala.reflect.runtime.{universe => ru}

/** Binder for values extending [[scala.Enumeration]]
  */
class EnumerationBinder(runtimeType: ru.Type, mirror: ru.Mirror, config: BindingConfig) extends TypedBinder[Any] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]], bindingMetadata: Map[String, Any]): BindingResult[Any] = {
    val enumType = runtimeType.asInstanceOf[ru.TypeRef].pre
    val classSymbol = enumType.typeSymbol.asClass
    val companionSymbol = classSymbol.companionSymbol.asModule
    val objectInstance = mirror.reflectModule(companionSymbol).instance
    val applySymbol = enumType.member(ru.TermName("apply")).asMethod
    val applyMethod = mirror.reflect(objectInstance).reflectMethod(applySymbol)
    try {
      BindingPass(applyMethod(valueMap(fieldName).headOption.map(_.trim).filterNot(_.isEmpty).map(_.toInt).get))
    } catch {
      case ex: InvocationTargetException => BindingFailure(fieldName, config.languageConfig.invalidEnumerationMessage(fieldName, valueMap.get(fieldName).flatMap(_.headOption).getOrElse("")), Some(ex))
      case ex: NumberFormatException => BindingFailure(fieldName, config.languageConfig.invalidEnumerationMessage(fieldName, valueMap.get(fieldName).flatMap(_.headOption).getOrElse("")), Some(ex))
      case ex: NoSuchElementException => BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName), Some(ex))
    }
  }


}
