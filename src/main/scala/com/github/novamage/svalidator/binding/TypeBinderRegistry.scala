package com.github.novamage.svalidator.binding

import scala.reflect.runtime.{universe => ru}
import scala.collection.mutable.ListBuffer
import com.github.novamage.svalidator.binding.binders.typed._
import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.binders.special._
import scala.Some

object TypeBinderRegistry {

  private val directBinders = ListBuffer[(TypedBinder[_], ru.TypeTag[_])]()
  private var currentBindingConfig: BindingConfig = BindingConfig.defaultConfig
  private val recursiveBinders = ListBuffer[(TypedBinder[_], ru.TypeTag[_])]()

  def initializeBinders() {
    initializeBinders(BindingConfig.defaultConfig)
  }

  def initializeBinders(config: BindingConfig) {
    clearBinderBuffers()
    registerBinder(new StringBinder(config))
    registerBinder(new IntBinder(config))
    registerBinder(new LongBinder(config))
    registerBinder(new FloatBinder(config))
    registerBinder(new DoubleBinder(config))
    registerBinder(new BigDecimalBinder(config))
    registerBinder(new BooleanBinder(config))
    registerBinder(new TimestampBinder(config))
    currentBindingConfig = config
  }

  def clearBinders() {
    clearBinderBuffers()
  }

  private def clearBinderBuffers() {
    directBinders.clear()
    recursiveBinders.clear()
  }

  protected[binding] def getBinderForType(runtimeType: ru.Type, mirror: ru.Mirror): Option[TypedBinder[_]] = {
    val directBinderOption = directBinders collectFirst {
      case (binder, tag) if tag.tpe =:= runtimeType => binder
    }
    if (directBinderOption.isDefined) {
      directBinderOption
    } else if (runtimeType.asInstanceOf[ru.TypeRef].pre.baseClasses.exists(x => x == ru.typeOf[Enumeration].typeSymbol.asClass)) {
      Some(new EnumerationBinder(runtimeType, mirror, currentBindingConfig))
    }
    else if (runtimeType.erasure =:= ru.typeOf[Option[_]].erasure) {
      getBinderForType(runtimeType.asInstanceOf[ru.TypeRef].args.head, mirror).map(new OptionBinderWrapper(_))
    }
    else if (runtimeType.erasure =:= ru.typeOf[List[_]].erasure) {
      getBinderForType(runtimeType.asInstanceOf[ru.TypeRef].args.head, mirror).map(new ListBinderWrapper(_))
    }
    else if (isTypeACaseObjectEnum(runtimeType)) {
      Some(new ObjectBasedEnumBinder(runtimeType, mirror, currentBindingConfig))
    } else {
      recursiveBinders collectFirst {
        case (binder, tag) if tag.tpe =:= runtimeType => binder
      }
    }


  }


  private def isTypeACaseObjectEnum(runtimeType: ru.Type): Boolean = {
    //The criteria applied here is that
    // the class must be sealed,
    // it must have only one constructor,
    // the first argument of said constructor must be an int,
    // all known descendants must be enclosed within the companion object of the class
    // and there must exist a getter method for said argument which is either public or protected
    val classSymbol = runtimeType.typeSymbol.asClass
    if (!classSymbol.isSealed)
      return false

    val constructor = runtimeType.declaration(ru.nme.CONSTRUCTOR).asTerm
    if (constructor.isOverloaded)
      return false

    val params = constructor.asMethod.paramss.flatten
    if (params.isEmpty || !(params.head.typeSignature =:= ru.typeOf[Int]))
      return false

    val companionObject = classSymbol.companionSymbol.asModule.moduleClass.asType
    val allKnownDescendants = classSymbol.knownDirectSubclasses
    if (!allKnownDescendants.forall(x => x.owner == companionObject))
      return false

    val leadingIntParamName = params.head.asTerm.name.decoded
    val targetTermName = ru.newTermName(leadingIntParamName)
    val getter = runtimeType.members.filter(x => x.name == targetTermName && x.isMethod).map(_.asMethod) find {
      x => (x.isPublic || x.isProtected) && x.isGetter && x.isParamAccessor
    }
    getter.isDefined
  }

  def registerBinder[A](binder: TypedBinder[A])(implicit tag: ru.TypeTag[A]) {
    directBinders.append((binder, tag))
  }

  def allowRecursiveBindingForType[A]()(implicit tag: ru.TypeTag[A]) {
    recursiveBinders.append((new RecursiveBinderWrapper[A](), tag))
  }

}
