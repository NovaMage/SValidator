package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding.binders.ITypedBinder
import scala.reflect.runtime.{universe => ru}
import com.github.novamage.svalidator.binding.{BindingPass, BindingResult, BindingConfig, BindingFailure}

class ObjectBasedEnumBinder(runtimeType: ru.Type, mirror: ru.Mirror, config: BindingConfig) extends ITypedBinder[Any] {

  override def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[Any] = {
    try {
      val intValue = valueMap(fieldName).headOption.map(_.trim).filterNot(_.isEmpty).map(_.toInt).get
      val classSymbol = runtimeType.typeSymbol.asClass
      val constructor = runtimeType.declaration(ru.nme.CONSTRUCTOR).asMethod
      val leadingIntTermName = ru.newTermName(constructor.paramss.flatten.head.asTerm.name.decoded)

      val knownDescendants = classSymbol.knownDirectSubclasses.toIterable
      val matchedCaseObjectOption = knownDescendants map {
        descendantType =>
          val companionModuleSymbol = descendantType.companionSymbol.asModule
          val singleIntParamGetterMethodSymbol = (descendantType.typeSignature.members filter {
            x => x.name == leadingIntTermName && x.isMethod
          } map { _.asMethod } find {
            x => (x.isPublic || x.isProtected) && x.isGetter && x.isParamAccessor
          }).get
          val reflectedCompanion = mirror.reflectModule(companionModuleSymbol)
          val instance = reflectedCompanion.instance
          val instanceMirror = mirror.reflect(instance)
          val getterMethod = instanceMirror.reflectMethod(singleIntParamGetterMethodSymbol)
          getterMethod() -> instance
      } collectFirst {
        case (enumValue, instance) if enumValue == intValue => instance
      }
      matchedCaseObjectOption match {
        case Some(caseObjectEnum) => BindingPass(caseObjectEnum)
        case None => new BindingFailure(fieldName, config.languageConfig.invalidEnumerationMessage(fieldName), None)
      }
    } catch {
      case ex: NumberFormatException => new BindingFailure(fieldName, config.languageConfig.invalidEnumerationMessage(fieldName), Some(ex))
      case ex: NoSuchElementException => new BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName), Some(ex))
    }
  }
}
