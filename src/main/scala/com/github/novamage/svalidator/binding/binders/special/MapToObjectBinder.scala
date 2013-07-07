package com.github.novamage.svalidator.binding.binders.special

import scala.reflect.runtime.{universe => ru}
import scala.collection.mutable.ListBuffer
import com.github.novamage.svalidator.binding.exceptions.NoBinderFoundException
import com.github.novamage.svalidator.binding._
import com.github.novamage.svalidator.binding.BindingPass
import com.github.novamage.svalidator.binding.FieldError
import scala.Some


object MapToObjectBinder {

  private def normalizeKeys(map: Map[String, Seq[String]]): Map[String, Seq[String]] = {
    map map {
      case (key, value) => {
        val dotNotationKey = key.replace("]", "").replace("[", ".")
        val tokens = dotNotationKey.split("\\.")
        val normalizedKey = tokens.zipWithIndex map {
          case (element, index) => if (index == 0) element else if (element.forall(_.isDigit)) "[" + element + "]" else "." + element
        } mkString ""
        (normalizedKey, value)
      }
    }
  }

  def bind[T: ru.TypeTag](dataMap: Map[String, Seq[String]]): BindingResult[T] = {
    bind[T](None, dataMap)
  }

  protected[special] def bind[T: ru.TypeTag](fieldPrefix: Option[String], dataMap: Map[String, Seq[String]]): BindingResult[T] = {
    val normalizedMap = normalizeKeys(dataMap)
    val tag = ru.typeTag[T]
    val runtimeMirror = tag.mirror
    val runtimeType = tag.tpe
    val classToBind = runtimeType.typeSymbol.asClass
    val constructor = runtimeType.declaration(ru.nme.CONSTRUCTOR).asMethod
    val paramSymbols = constructor.paramss
    val argList = ListBuffer[Any]()
    val errorList = ListBuffer[FieldError]()
    val prefix = fieldPrefix.map(_ + ".").getOrElse("")
    paramSymbols.flatten foreach {
      symbol =>
        val paramTermSymbol = symbol.asTerm
        val parameterName = prefix + paramTermSymbol.name.decoded
        val parameterType = paramTermSymbol.typeSignature
        val typeBinder = TypeBinderRegistry.getBinderForType(parameterType, runtimeMirror)
        typeBinder match {
          case Some(binder) => {
            binder.bind(parameterName, normalizedMap) match {
              case BindingPass(value) => argList.append(value)
              case BindingFailure(errors) => errorList.appendAll(errors)
            }
          }
          case None => throw new NoBinderFoundException(parameterType)
        }
    }


    errorList.toList match {
      case Nil => {
        val reflectClass = runtimeMirror.reflectClass(classToBind)
        val constructorMirror = reflectClass.reflectConstructor(constructor)
        BindingPass(constructorMirror.apply(argList.toList: _*).asInstanceOf[T])
      }
      case nonEmptyList => BindingFailure[T](nonEmptyList)
    }
  }

}