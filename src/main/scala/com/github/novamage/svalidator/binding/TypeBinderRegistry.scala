package com.github.novamage.svalidator.binding

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.binders.special._
import com.github.novamage.svalidator.binding.binders.typed._

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.{universe => ru}

object TypeBinderRegistry {


  private val directBinders = ListBuffer[(TypedBinder[_], ru.TypeTag[_])]()
  private val recursiveBinders = ListBuffer[(TypedBinder[_], ru.TypeTag[_])]()
  private var currentBindingConfig: BindingConfig = BindingConfig.defaultConfig

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

  private def clearBinderBuffers() {
    directBinders.clear()
    recursiveBinders.clear()
  }

  def registerBinder[A](binder: TypedBinder[A])(implicit tag: ru.TypeTag[A]) {
    directBinders.append((binder, tag))
  }

  def clearBinders() {
    clearBinderBuffers()
  }

  def allowRecursiveBindingForType[A]()(implicit tag: ru.TypeTag[A]) {
    recursiveBinders.append((new RecursiveBinder[A](), tag))
  }

  protected[binding] def getBinderForType(runtimeType: ru.Type, mirror: ru.Mirror, allowRecursiveBinders: Boolean = true): Option[TypedBinder[_]] = {
    val directBinderOption = directBinders collectFirst {
      case (binder, tag) if tag.tpe =:= runtimeType => binder
    }
    if (directBinderOption.isDefined) {
      directBinderOption
    } else if (runtimeType.asInstanceOf[ru.TypeRef].pre.baseClasses.contains(ru.typeOf[Enumeration].typeSymbol.asClass)) {
      Some(new EnumerationBinder(runtimeType, mirror, currentBindingConfig))
    }
    else if (runtimeType.erasure =:= ru.typeOf[Option[_]].erasure) {
      getBinderForType(runtimeType.asInstanceOf[ru.TypeRef].args.head, mirror).map(new OptionBinder(_))
    }
    else if (runtimeType.erasure =:= ru.typeOf[List[_]].erasure) {
      getBinderForType(runtimeType.asInstanceOf[ru.TypeRef].args.head, mirror).map(new ListBinder(_))
    }
    else if (runtimeType.erasure =:= ru.typeOf[Set[_]].erasure) {
      getBinderForType(runtimeType.asInstanceOf[ru.TypeRef].args.head, mirror).map(new SetBinder(_))
    }
    else if (isTypeATypeBasedEnum(runtimeType)) {
      Some(new TypeBasedEnumerationBinder(runtimeType, mirror, currentBindingConfig))
    } else if (allowRecursiveBinders) {
      recursiveBinders collectFirst {
        case (binder, tag) if tag.tpe =:= runtimeType => binder
      }
    } else {
      None
    }


  }

  private def isTypeATypeBasedEnum(runtimeType: ru.Type): Boolean = {
    //The criteria applied here is that
    // the type must be sealed and have at least one known descendant,
    // the type must be abstract,
    // the type must have a companion symbol
    // the type must have a primary constructor
    // the first argument of said constructor must be an int,
    // all direct known descendants must be enclosed within the companion object of the type
    // all descendants must be module classes (i.e. object definitions that extend the runtimeType)
    // and there must exist a getter/param accessor method for said int argument which is either public or protected
    val classSymbol = runtimeType.typeSymbol.asClass
    val allKnownDescendants = classSymbol.knownDirectSubclasses.map(_.asClass).toVector

    //by testing if the class has known descendants, we also implicitly test that it is sealed
    if (allKnownDescendants.isEmpty || !classSymbol.isAbstract || !classSymbol.companion.isModule)
      return false

    val constructorSymbols = runtimeType.decl(ru.termNames.CONSTRUCTOR)
    if (!constructorSymbols.isTerm)
      return false

    val primaryConstructorMethodOption = constructorSymbols.asTerm.alternatives.collectFirst {
      case ctor if ctor.asMethod.isPrimaryConstructor => ctor.asMethod
    }
    if (primaryConstructorMethodOption.isEmpty)
      return false

    val constructor = primaryConstructorMethodOption.get
    val params = constructor.paramLists.flatten
    if (params.isEmpty || !(params.head.typeSignature =:= ru.typeOf[Int]))
      return false

    val companionObject = classSymbol.companion.asModule.moduleClass.asType
    if (allKnownDescendants.exists(x => x.owner != companionObject) || allKnownDescendants.exists(!_.isModuleClass))
      return false

    val leadingIntParamName = params.head.asTerm.name.decodedName.toString
    val targetTermName = ru.TermName(leadingIntParamName)
    val getter = runtimeType.members.filter(x => x.name == targetTermName && x.isMethod).map(_.asMethod) find {
      x => (x.isPublic || x.isProtected) && x.isGetter && x.isParamAccessor
    }
    getter.isDefined
  }

}
