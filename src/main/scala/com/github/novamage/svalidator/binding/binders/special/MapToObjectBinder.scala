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
      case (key, value) if key.contains("[") =>
        val dotNotationKey = key.replace("]", "").replace("[", ".")
        val tokens = dotNotationKey.split("\\.")
        val normalizedKey = tokens.zipWithIndex map {
          case (element, index) => if (index == 0) element else if (element.forall(_.isDigit)) "[" + element + "]" else "." + element
        } mkString ""
        (normalizedKey, value)
      case (key, value) => (key, value)
    }
  }

  def bind[T: ru.TypeTag](dataMap: Map[String, Seq[String]]): BindingResult[T] = {
    val normalizedMap = normalizeKeys(dataMap)
    bind[T](None, normalizedMap)
  }

  protected[special] def bind[T: ru.TypeTag](fieldPrefix: Option[String], normalizedMap: Map[String, Seq[String]]): BindingResult[T] = {
    val tag = ru.typeTag[T]
    val runtimeMirror = tag.mirror
    val runtimeType = tag.tpe
    val classToBind = runtimeType.typeSymbol.asClass
    val constructor = runtimeType.declaration(ru.nme.CONSTRUCTOR).asMethod
    val paramSymbols = constructor.paramss
    val argList = ListBuffer[Any]()
    val errorList = ListBuffer[FieldError]()
    val causeList = ListBuffer[Throwable]()
    val prefix = fieldPrefix.map(_ + ".").getOrElse("")
    paramSymbols.flatten foreach {
      symbol =>
        val paramTermSymbol = symbol.asTerm
        val parameterName = prefix + paramTermSymbol.name.decoded
        val parameterType = paramTermSymbol.typeSignature
        val typeBinder = TypeBinderRegistry.getBinderForType(parameterType, runtimeMirror)
        typeBinder match {
          case Some(binder) =>
            binder.bind(parameterName, normalizedMap) match {
              case BindingPass(value) => argList.append(value)
              case BindingFailure(errors, cause) =>
                errorList.appendAll(errors)
                cause.map(x => causeList.append(x))
            }
          case None => throw new NoBinderFoundException(parameterType)
        }
    }


    errorList.toList match {
      case Nil =>
        val reflectClass = runtimeMirror.reflectClass(classToBind)
        val constructorMirror = reflectClass.reflectConstructor(constructor)
        BindingPass(constructorMirror.apply(argList.toList: _*).asInstanceOf[T])
      case nonEmptyList =>
        if (argList.filterNot(x => x == None || x == false).isEmpty && causeList.forall(_.isInstanceOf[NoSuchElementException])) {
          BindingFailure[T](nonEmptyList, Some(new NoSuchElementException()))
        } else {
          BindingFailure[T](nonEmptyList, None)
        }
    }
  }

}
