package com.github.novamage.svalidator.binding

import scala.reflect.runtime.{universe => ru}
import scala.collection.mutable.ListBuffer


class MapToObjectBinder {

  def performBind[T](dataMap: Map[String, Seq[String]])(implicit tag: ru.TypeTag[T]): BindingResult[T] = {
    val mirror = ru.runtimeMirror(tag.tpe.getClass.getClassLoader)
    val scalaType = tag.tpe
    val classPerson = scalaType.typeSymbol.asClass
    val reflectClass = mirror.reflectClass(classPerson)
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
          case None => throw new Exception("No binder found for type:" + parameterType.toString)
        }
    }

    val constructorMirror = reflectClass.reflectConstructor(constructor)

    if (errorList.isEmpty) {
      BindingPass(constructorMirror.apply(argList.toList: _*).asInstanceOf[T])
    }
    else {
      BindingFailure[T](errorList.toList)
    }
  }

}
