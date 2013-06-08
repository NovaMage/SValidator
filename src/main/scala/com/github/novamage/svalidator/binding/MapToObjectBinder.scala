package com.github.novamage.svalidator.binding

import scala.reflect.runtime.{universe => ru}
import scala.collection.mutable.ListBuffer


object MapToObjectBinder {

  def performBind[T: ru.TypeTag](dataMap: Map[String, Seq[String]]): BindingResult[T] = {
    val tag = ru.typeTag[T]
    val mirror = ru.runtimeMirror(getClass.getClassLoader)
    val scalaType = tag.tpe
    val classToBind = scalaType.typeSymbol.asClass
    val reflectClass = mirror.reflectClass(classToBind)
    val constructor = scalaType.declaration(ru.nme.CONSTRUCTOR).asMethod
    val paramSymbols = constructor.paramss
    val argList = ListBuffer[Any]()
    val errorList = ListBuffer[FieldError]()
    paramSymbols.flatten foreach {
      symbol =>
        val paramTermSymbol = symbol.asTerm
        val parameterName = paramTermSymbol.name.decoded
        val parameterType = paramTermSymbol.typeSignature
        val typeBinder = TypeBinderRegistry.getBinderForType(parameterType)
        typeBinder match {
          case Some(binder) => {
            binder.bind(parameterName, dataMap) match {
              case BindingPass(value) => argList.append(value)
              case BindingFailure(errors) => errorList.appendAll(errors)
            }
          }
          case None => throw new Exception("No binder found for type: " + parameterType.toString)
        }
    }


    errorList.toList match {
      case Nil => {
        val constructorMirror = reflectClass.reflectConstructor(constructor)
        BindingPass(constructorMirror.apply(argList.toList: _*).asInstanceOf[T])
      }
      case nonEmptyList => BindingFailure[T](nonEmptyList)
    }
  }

}
