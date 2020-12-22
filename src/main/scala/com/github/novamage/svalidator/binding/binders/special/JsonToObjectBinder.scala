package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding._
import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}
import com.github.novamage.svalidator.binding.exceptions.{NoBinderFoundException, NoDirectBinderNorConstructorForBindingException}
import com.github.novamage.svalidator.binding.internals.{JsonReflectiveBinderInformation, JsonReflectiveParamInformation, ReflectiveBinderInformation, ReflectiveParamInformation}
import io.circe.ACursor

import scala.reflect.runtime.{universe => ru}

/** Provides methods for binding instances by scanning through available
  * [[com.github.novamage.svalidator.binding.binders.TypedBinder TypedBinder]]s or using reflection if possible/necessary.
  */
object JsonToObjectBinder {

  /** Attempts to bind to the target parameter type using the given values map, and given field prefix.
    *
    * This method will scan first for available json direct binders of the type.  If none is found, then it will be attempted
    * to reflectively bind the parameters of its primary constructor, if one is available.  If reflection binding is
    * successful, the information gained through runtime reflection is stored in a special json binder and will be used for any
    * future bindings of the same type, to avoid incurring the cost of repeated reflection.
    *
    * @param inputJson Json string to parse and bind values from
    * @param globalFieldName Prefix to be prepended to all field names when scanning for values
    * @tparam A Type being bound
    * @return BindingPass with the bound value if successful, BindingFailure with errors and throwable cause otherwise
    */
  def bind[A](inputJson: String, globalFieldName: Option[String] = None, bindingMetadata: Map[String, Any] = Map.empty)(implicit tag: ru.TypeTag[A]): BindingResult[A] = {
    val parsingResult = io.circe.parser.parse(inputJson)
    parsingResult match {
      case Left(parsingFailure) =>
        BindingFailure(globalFieldName.getOrElse(""), TypeBinderRegistry.currentBindingConfig.languageConfig.invalidJsonMessage(globalFieldName, inputJson), Some(parsingFailure))
      case Right(json) =>
        val typeBinderOption = TypeBinderRegistry.getJsonBinderForType(tag.tpe, tag.mirror, allowRecursiveBinders = false)
        typeBinderOption.map(_.asInstanceOf[JsonTypedBinder[A]].bindJson(json.hcursor, globalFieldName.getOrElse(""), bindingMetadata)).getOrElse(bind[A](json.hcursor, globalFieldName.map(_.trim).filterNot(_.isEmpty), bindingMetadata))
    }
  }

  protected[special] def bind[T](currentCursor: ACursor, fieldPrefix: Option[String], bindingMetadata: Map[String, Any])(implicit tag: ru.TypeTag[T]): BindingResult[T] = {
    val reflectiveBinder = buildAndRegisterJsonReflectiveBinderFor(tag)
    reflectiveBinder.bindJson(currentCursor, fieldPrefix.getOrElse(""), bindingMetadata)
  }

  protected[binding] def buildAndRegisterJsonReflectiveBinderFor[T](tag: ru.TypeTag[T]): JsonReflectivelyBuiltDirectBinder[T] = {
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
    val constructorTypeSignature = primaryConstructorMethod.typeSignatureIn(runtimeType)
    val paramSymbols = constructorTypeSignature.paramLists
    val reflectiveParamsInfo = paramSymbols.flatten.map {
      symbol =>
        val paramTermSymbol = symbol.asTerm
        val constructorParamName = paramTermSymbol.name.decodedName.toString
        val parameterType = paramTermSymbol.typeSignature
        val typeBinder = TypeBinderRegistry.getJsonBinderForType(parameterType, runtimeMirror)
        typeBinder match {
          case Some(binder) =>
            new JsonReflectiveParamInformation(constructorParamName, binder)
          case None => throw new NoBinderFoundException(parameterType)
        }
    }

    val classToBind = runtimeType.typeSymbol.asClass
    val reflectClass = runtimeMirror.reflectClass(classToBind)
    val constructorMirror = reflectClass.reflectConstructor(primaryConstructorMethod)
    val binderInformation = new JsonReflectiveBinderInformation(constructorMirror, reflectiveParamsInfo)
    val jsonReflectiveBinder = new JsonReflectivelyBuiltDirectBinder[T](binderInformation)
    TypeBinderRegistry.registerJsonBinder[T](jsonReflectiveBinder)(tag)
    jsonReflectiveBinder
  }

}
