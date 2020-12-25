package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass, BindingResult}
import io.circe.ACursor

import java.lang.reflect.InvocationTargetException
import scala.reflect.runtime.{universe => ru}

/** Binder for values extending [[scala.Enumeration]]
  */
class EnumerationBinder(runtimeType: ru.Type, mirror: ru.Mirror, config: BindingConfig)
  extends TypedBinder[Any] with JsonTypedBinder[Any] {

  override def bind(fieldName: String, valueMap: Map[String, Seq[String]], bindingMetadata: Map[String, Any]): BindingResult[Any] = {
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

  override def bindJson(currentCursor: ACursor, fieldName: String, bindingMetadata: Map[String, Any]): BindingResult[Any] = {
    try {
      currentCursor.as[Option[Int]] match {
        case Left(decodingFailure) =>
          val errorValue = currentCursor.focus.map(_.toString()).getOrElse("")
          BindingFailure(fieldName, config.languageConfig.invalidEnumerationMessage(fieldName, errorValue), Some(decodingFailure))
        case Right(value) =>
          val enumType = runtimeType.asInstanceOf[ru.TypeRef].pre
          val classSymbol = enumType.typeSymbol.asClass
          val companionSymbol = classSymbol.companionSymbol.asModule
          val objectInstance = mirror.reflectModule(companionSymbol).instance
          val applySymbol = enumType.member(ru.TermName("apply")).asMethod
          val applyMethod = mirror.reflect(objectInstance).reflectMethod(applySymbol)
          BindingPass(applyMethod(value.get))
      }
    } catch {
      case ex: InvocationTargetException => BindingFailure(fieldName, config.languageConfig.invalidEnumerationMessage(fieldName, currentCursor.focus.map(_.toString()).getOrElse("")), Some(ex))
      case ex: NoSuchElementException => BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName), Some(ex))
    }
  }
}
