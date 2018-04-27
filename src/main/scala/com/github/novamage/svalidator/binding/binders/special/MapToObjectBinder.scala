package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.exceptions.{NoBinderFoundException, NoDirectBinderNorConstructorForBindingException}
import com.github.novamage.svalidator.binding.{BindingPass, FieldError, _}
import com.github.novamage.svalidator.validation.binding.BindingLocalizer

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.{universe => ru}


object MapToObjectBinder {

  def bind[A](dataMap: Map[String, Seq[String]], localizer: BindingLocalizer, globalFieldName: Option[String] = None)(implicit tag: ru.TypeTag[A]): BindingResult[A] = {
    val normalizedMap = normalizeKeys(dataMap)
    val typeBinderOption = TypeBinderRegistry.getBinderForType(tag.tpe, tag.mirror, allowRecursiveBinders = false)
    typeBinderOption.map(_.asInstanceOf[TypedBinder[A]].bind(globalFieldName.getOrElse(""), normalizedMap, localizer)).getOrElse(bind[A](globalFieldName.filterNot(_.isEmpty), normalizedMap, localizer))
  }

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

  protected[special] def bind[T](fieldPrefix: Option[String], normalizedMap: Map[String, Seq[String]], localizer: BindingLocalizer)(implicit tag: ru.TypeTag[T]): BindingResult[T] = {
    val runtimeMirror = tag.mirror
    val runtimeType = tag.tpe
    val constructorSymbols = runtimeType.decl(ru.termNames.CONSTRUCTOR)
    if (!constructorSymbols.isTerm) {
      throw new NoDirectBinderNorConstructorForBindingException(runtimeType)
    }
    val constructorMethodOption = constructorSymbols.asTerm.alternatives.collectFirst {
      case ctor if ctor.asMethod.isPrimaryConstructor => ctor.asMethod
    }
    if (constructorMethodOption.isEmpty) {
      throw new NoDirectBinderNorConstructorForBindingException(runtimeType)
    }
    val primaryConstructorMethod = constructorMethodOption.get
    val paramSymbols = primaryConstructorMethod.paramLists
    val argList = ListBuffer[Any]()
    val errorList = ListBuffer[FieldError]()
    val causeList = ListBuffer[Throwable]()
    val prefix = fieldPrefix.map(_ + ".").getOrElse("")
    paramSymbols.flatten foreach {
      symbol =>
        val paramTermSymbol = symbol.asTerm
        val parameterName = prefix + paramTermSymbol.name.decodedName.toString
        val parameterType = paramTermSymbol.typeSignature
        val typeBinder = TypeBinderRegistry.getBinderForType(parameterType, runtimeMirror)
        typeBinder match {
          case Some(binder) =>
            binder.bind(parameterName, normalizedMap, localizer) match {
              case BindingPass(value) => argList.append(value)
              case BindingFailure(errors, cause) =>
                errorList.appendAll(errors)
                cause.foreach(causeList.append(_))
            }
          case None => throw new NoBinderFoundException(parameterType)
        }
    }


    errorList.toList match {
      case Nil =>
        val classToBind = runtimeType.typeSymbol.asClass
        val reflectClass = runtimeMirror.reflectClass(classToBind)
        val constructorMirror = reflectClass.reflectConstructor(primaryConstructorMethod)
        BindingPass(constructorMirror.apply(argList.toList: _*).asInstanceOf[T])
      case nonEmptyList =>
        if (argList.forall(x => x == None || x == false) && causeList.forall(_.isInstanceOf[NoSuchElementException])) {
          BindingFailure[T](nonEmptyList, Some(new NoSuchElementException()))
        } else {
          BindingFailure[T](nonEmptyList, None)
        }
    }
  }

}
