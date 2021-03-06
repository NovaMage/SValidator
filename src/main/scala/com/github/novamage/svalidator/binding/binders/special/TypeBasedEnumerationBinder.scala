package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass, BindingResult}

import scala.reflect.runtime.{universe => ru}

/** Binder for classes that follow the case-object style described in
  * [[https://github.com/NovaMage/SValidator/wiki/Type-Based-Enumerations Type Based Enumerations]].
  */
class TypeBasedEnumerationBinder(runtimeType: ru.Type, mirror: ru.Mirror, config: BindingConfig) extends TypedBinder[Any] {

  override def bind(fieldName: String, valueMap: Map[String, Seq[String]], bindingMetadata: Map[String, Any]): BindingResult[Any] = {
    val receivedValue = valueMap.getOrElse(fieldName, Nil).headOption.map(_.trim).filterNot(_.isEmpty)
    try {
      val intValue = receivedValue.map(_.toInt).get
      val classSymbol = runtimeType.typeSymbol.asClass
      val constructorSymbols = runtimeType.decl(ru.termNames.CONSTRUCTOR)
      val constructor = constructorSymbols.asTerm.alternatives.collectFirst {
        case ctor if ctor.asMethod.isPrimaryConstructor => ctor.asMethod
      }.get

      val leadingIntTermName = ru.TermName(constructor.paramLists.flatten.head.asTerm.name.decodedName.toString)

      val enclosingModule = classSymbol.companion.asModule
      val reflectedEnclosingModule = mirror.reflectModule(enclosingModule)
      val enclosingObjectInstance = reflectedEnclosingModule.instance
      val enclosingInstanceMirror = mirror.reflect(enclosingObjectInstance)
      val enclosingInstanceSymbol = enclosingInstanceMirror.symbol
      val knownDescendants = classSymbol.knownDirectSubclasses
      val matchedCaseObjectOption = knownDescendants map {
        descendantType =>
          val innerObjectModule = enclosingInstanceSymbol.typeSignature.member(ru.TermName(descendantType.name.decodedName.toString)).asModule
          val companionObjectSymbol = innerObjectModule.moduleClass.asClass
          val singleIntParamGetterMethodSymbol = (innerObjectModule.typeSignature.members filter {
            x => x.name == leadingIntTermName && x.isMethod
          } map { _.asMethod } find {
            x => (x.isPublic || x.isProtected) && x.isGetter && x.isParamAccessor
          }).get
          val reflectedCompanion = mirror.reflectClass(companionObjectSymbol)
          val companionConstructorTerm = companionObjectSymbol.typeSignature.decl(ru.termNames.CONSTRUCTOR).asMethod
          val objectInstance = reflectedCompanion.reflectConstructor(companionConstructorTerm).apply()
          val instanceMirror = mirror.reflect(objectInstance)
          val getterMethod = instanceMirror.reflectMethod(singleIntParamGetterMethodSymbol)
          getterMethod() -> objectInstance
      } collectFirst {
        case (enumValue, instance) if enumValue == intValue => instance
      }
      matchedCaseObjectOption match {
        case Some(caseObjectEnum) => BindingPass(caseObjectEnum)
        case None => BindingFailure(fieldName, config.languageConfig.invalidEnumerationMessage(fieldName, receivedValue.getOrElse("")), None)
      }
    } catch {
      case ex: NumberFormatException => BindingFailure(fieldName, config.languageConfig.invalidEnumerationMessage(fieldName, receivedValue.getOrElse("")), Some(ex))
      case ex: NoSuchElementException => BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName), Some(ex))
    }
  }
}
