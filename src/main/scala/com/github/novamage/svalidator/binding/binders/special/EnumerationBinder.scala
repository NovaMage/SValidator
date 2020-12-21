package com.github.novamage.svalidator.binding.binders.special

import java.lang.reflect.InvocationTargetException
import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass, BindingResult}
import io.circe.{DecodingFailure, HCursor}

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

  override def bind(currentCursor: HCursor, fieldName: String, bindingMetadata: Map[String, Any]): BindingResult[Any] = {
    try {
      currentCursor.as[Int] match {
        case Left(decodingFailure) =>
          val errorValue = currentCursor.as[Option[String]].toOption.flatten.get
          BindingFailure(fieldName, config.languageConfig.invalidEnumerationMessage(fieldName, errorValue), Some(decodingFailure))
        case Right(value) =>
          val enumType = runtimeType.asInstanceOf[ru.TypeRef].pre
          val classSymbol = enumType.typeSymbol.asClass
          val companionSymbol = classSymbol.companionSymbol.asModule
          val objectInstance = mirror.reflectModule(companionSymbol).instance
          val applySymbol = enumType.member(ru.TermName("apply")).asMethod
          val applyMethod = mirror.reflect(objectInstance).reflectMethod(applySymbol)
          BindingPass(applyMethod(value))
      }
    } catch {
      case ex: InvocationTargetException => BindingFailure(fieldName, config.languageConfig.invalidEnumerationMessage(fieldName, currentCursor.as[Option[String]].toOption.flatten.getOrElse("")), Some(ex))
      case ex: NoSuchElementException => BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName), Some(ex))
    }
  }
}
