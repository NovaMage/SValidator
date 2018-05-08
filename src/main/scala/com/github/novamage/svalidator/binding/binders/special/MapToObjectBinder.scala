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

  private def normalizeKeys(valuesMap: Map[String, Seq[String]]): Map[String, Seq[String]] = {
    valuesMap map {
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
    val reflectiveParamsInfo = paramSymbols.flatten.map {
      symbol =>
        val paramTermSymbol = symbol.asTerm
        val constructorParamName = paramTermSymbol.name.decodedName.toString
        val parameterType = paramTermSymbol.typeSignature
        val typeBinder = TypeBinderRegistry.getBinderForType(parameterType, runtimeMirror)
        typeBinder match {
          case Some(binder) =>
            new ReflectiveParamInformation(constructorParamName, binder)
          case None => throw new NoBinderFoundException(parameterType)
        }
    }

    val classToBind = runtimeType.typeSymbol.asClass
    val reflectClass = runtimeMirror.reflectClass(classToBind)
    val constructorMirror = reflectClass.reflectConstructor(primaryConstructorMethod)
    val binderInformation = new ReflectiveBinderInformation(constructorMirror, reflectiveParamsInfo)
    val reflectiveBinder = new ReflectivelyBuiltDirectBinder[T](binderInformation)
    TypeBinderRegistry.registerBinder[T](reflectiveBinder)(tag)
    reflectiveBinder.bind(fieldPrefix.getOrElse(""), normalizedMap, localizer)
  }

}
