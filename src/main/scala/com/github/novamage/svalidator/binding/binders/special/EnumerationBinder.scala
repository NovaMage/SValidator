package com.github.novamage.svalidator.binding.binders.special

import java.lang.reflect.InvocationTargetException

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass}

import scala.reflect.runtime.{universe => ru}

class EnumerationBinder(runtimeType: ru.Type, mirror: ru.Mirror, config: BindingConfig) extends TypedBinder[Any] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]]) = {
    val enumType = runtimeType.asInstanceOf[ru.TypeRef].pre
    val companionObjectSymbol = enumType.typeSymbol.asClass
    val reflectedCompanion = mirror.reflectClass(companionObjectSymbol)
    val companionConstructorTerm = enumType.decl(ru.termNames.CONSTRUCTOR).asMethod
    val applySymbol = enumType.member(ru.TermName("apply")).asMethod
    val objectInstance = reflectedCompanion.reflectConstructor(companionConstructorTerm).apply()
    val applyMethod = mirror.reflect(objectInstance).reflectMethod(applySymbol)
    try {
      BindingPass(applyMethod(valueMap(fieldName).headOption.map(_.trim).filterNot(_.isEmpty).map(_.toInt).get))
    } catch {
      case ex: InvocationTargetException => new BindingFailure(fieldName, config.languageConfig.invalidEnumerationMessage(fieldName), Some(ex))
      case ex: NumberFormatException => new BindingFailure(fieldName, config.languageConfig.invalidEnumerationMessage(fieldName), Some(ex))
      case ex: NoSuchElementException => new BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName), Some(ex))
    }
  }

  def test() = {
  }
}
