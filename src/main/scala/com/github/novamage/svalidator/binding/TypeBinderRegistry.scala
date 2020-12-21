package com.github.novamage.svalidator.binding

import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}
import com.github.novamage.svalidator.binding.binders.special._
import com.github.novamage.svalidator.binding.binders.typed._
import com.github.novamage.svalidator.binding.hooks.{BeforeBindingAndValidatingHook, FailedBindingHook, SuccessfulBindingAndValidationHook, SuccessfulBindingFailedValidationHook}

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.{universe => ru}

/** Provides methods for initializing, registering or clearing binders
  */
object TypeBinderRegistry {

  private val directBinders = ListBuffer[(TypedBinder[_], ru.TypeTag[_])]()
  private val directJsonBinders = ListBuffer[(JsonTypedBinder[_], ru.TypeTag[_])]()
  private val allowedRecursiveBinders = ListBuffer[ru.TypeTag[_]]()
  protected[binding] var currentBindingConfig: BindingConfig = BindingConfig.defaultConfig

  protected[svalidator] val beforeBindingAndValidationHooks: ListBuffer[BeforeBindingAndValidatingHook] = ListBuffer[BeforeBindingAndValidatingHook]()
  protected[svalidator] val failedBindingHooks: ListBuffer[FailedBindingHook] = ListBuffer[FailedBindingHook]()
  protected[svalidator] val successfulBindingAndValidationHooks: ListBuffer[SuccessfulBindingAndValidationHook] = ListBuffer[SuccessfulBindingAndValidationHook]()
  protected[svalidator] val successfulBindingFailedValidationHooks: ListBuffer[SuccessfulBindingFailedValidationHook] = ListBuffer[SuccessfulBindingFailedValidationHook]()

  /** Initializes SValidator's default binders with the default binding configuration
    */
  def initializeBinders(): Unit = {
    initializeBinders(BindingConfig.defaultConfig)
  }

  /** Initializes SValidator's default binders with the passed in binding configuration
    */
  def initializeBinders(config: BindingConfig): Unit = {
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

  /** Adds the given binder to the list of registered binders, taking priority over any previously registered binders of
    * the same type
    *
    * @param binder Binder to register
    * @tparam A The type of instances bindable by the binder
    */
  def registerBinder[A](binder: TypedBinder[A])(implicit tag: ru.TypeTag[A]): Unit = {
    directBinders.prepend((binder, tag))
  }

  /** Adds the given binder to the list of registered json binders, taking priority over any previously registered json binders of
    * the same type
    *
    * @param binder Json binder to register
    * @tparam A The type of instances bindable by the binder
    */
  def registerBinder[A](binder: JsonTypedBinder[A])(implicit tag: ru.TypeTag[A]): Unit = {
    directJsonBinders.prepend((binder, tag))
  }

  /** Registers the given hook to be called globally before the binding step of
    * [[com.github.novamage.svalidator.validation.binding.MappingBindingValidatorWithData#bindAndValidate MappingBindingValidatorWithData.bindAndValidate]]
    * and its subclasses.
    *
    * Hooks will be called in the same order they are registered.  Hooking is NOT THREAD SAFE and should
    * be done during initialization code.
    */
  def registerBeforeBindingAndValidationHook(hook: BeforeBindingAndValidatingHook): Unit = {
    beforeBindingAndValidationHooks.append(hook)
  }

  /** Registers the given hook to be called globally after failing the binding step of
    * [[com.github.novamage.svalidator.validation.binding.MappingBindingValidatorWithData#bindAndValidate MappingBindingValidatorWithData.bindAndValidate]]
    * and its subclasses
    *
    * Hooks will be called in the same order they are registered.  Hooking is NOT THREAD SAFE and should
    * be done during initialization code.
    */
  def registerFailedBindingHook(hook: FailedBindingHook): Unit = {
    failedBindingHooks.append(hook)
  }

  /** Registers the given hook to be called globally after successfully binding but failing the validation step of
    * [[com.github.novamage.svalidator.validation.binding.MappingBindingValidatorWithData#bindAndValidate MappingBindingValidatorWithData.bindAndValidate]]
    * and its subclasses
    *
    * Hooks will be called in the same order they are registered.  Hooking is NOT THREAD SAFE and should
    * be done during initialization code.
    */
  def registerSuccessFulBindingFailedValidationHook(hook: SuccessfulBindingFailedValidationHook): Unit = {
    successfulBindingFailedValidationHooks.append(hook)
  }

  /** Registers the given hook to be called globally after successfully binding and validation steps of
    * [[com.github.novamage.svalidator.validation.binding.MappingBindingValidatorWithData#bindAndValidate MappingBindingValidatorWithData.bindAndValidate]]
    * and its subclasses
    *
    * Hooks will be called in the same order they are registered.  Hooking is NOT THREAD SAFE and should
    * be done during initialization code.
    */
  def registerSuccessFulBindingAndValidationHook(hook: SuccessfulBindingAndValidationHook): Unit = {
    successfulBindingAndValidationHooks.append(hook)
  }

  /** Removes all registered binders from this object
    */
  def clearBinders(): Unit = {
    clearBinderBuffers()
  }

  /** Removes all registered hooks from this object
    *
    * Note that hooking/clearing hooks IS NOT THREAD SAFE and the calling code must be responsible for
    * handling thread safety.
    */
  def clearHooks(): Unit = {
    beforeBindingAndValidationHooks.clear()
    failedBindingHooks.clear()
    successfulBindingFailedValidationHooks.clear()
    successfulBindingAndValidationHooks.clear()
  }

  /** Configures the registry to allow reflectively binding via constructor parameters if no binder is found for the
    * specified type, instead of throwing an exception.
    *
    * @tparam A Type that will be allowed to reflectively bind.
    */
  def allowRecursiveBindingForType[A]()(implicit tag: ru.TypeTag[A]): Unit = {
    allowedRecursiveBinders.prepend(tag)
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
      allowedRecursiveBinders collectFirst {
        case tag if tag.tpe =:= runtimeType => MapToObjectBinder.buildAndRegisterReflectiveBinderFor(tag)
      }
    } else {
      None
    }

  }

  protected[binding] def getJsonBinderForType(runtimeType: ru.Type, mirror: ru.Mirror, allowRecursiveBinders: Boolean = true): Option[JsonTypedBinder[_]] = {
    val directJsonBinderOption = directJsonBinders collectFirst {
      case (binder, tag) if tag.tpe =:= runtimeType => binder
    }
    if (directJsonBinderOption.isDefined) {
      directJsonBinderOption
    } else if (runtimeType.asInstanceOf[ru.TypeRef].pre.baseClasses.contains(ru.typeOf[Enumeration].typeSymbol.asClass)) {
      Some(new EnumerationBinder(runtimeType, mirror, currentBindingConfig))
    }
    else if (runtimeType.erasure =:= ru.typeOf[Option[_]].erasure) {
      getJsonBinderForType(runtimeType.asInstanceOf[ru.TypeRef].args.head, mirror).map(new JsonOptionBinder(_))
    }
    else if (runtimeType.erasure =:= ru.typeOf[List[_]].erasure) {
      getJsonBinderForType(runtimeType.asInstanceOf[ru.TypeRef].args.head, mirror).map(new JsonListBinder(_, currentBindingConfig))
    }
    else if (runtimeType.erasure =:= ru.typeOf[Set[_]].erasure) {
      getJsonBinderForType(runtimeType.asInstanceOf[ru.TypeRef].args.head, mirror).map(new JsonSetBinder(_, currentBindingConfig))
    }
    else if (isTypeATypeBasedEnum(runtimeType)) {
      Some(new TypeBasedEnumerationBinder(runtimeType, mirror, currentBindingConfig))
    } else if (allowRecursiveBinders) {
      allowedRecursiveBinders collectFirst {
        case tag if tag.tpe =:= runtimeType => MapToObjectBinder.buildAndRegisterJsonReflectiveBinderFor(tag)
      }
    } else {
      None
    }

  }


  private def clearBinderBuffers() {
    directBinders.clear()
    allowedRecursiveBinders.clear()
  }

  private def isTypeATypeBasedEnum(runtimeType: ru.Type): Boolean = {
    //The criteria applied here is that
    // the type must be a class
    // the type must be sealed and have at least one known descendant,
    // the type must be abstract,
    // the type must have a companion symbol
    // the type must have a primary constructor
    // the first argument of said constructor must be an int,
    // all direct known descendants must be enclosed within the companion object of the type
    // all descendants must be module classes (i.e. object definitions that extend the runtimeType)
    // and there must exist a getter/param accessor method for said int argument which is either public or protected
    if (!runtimeType.typeSymbol.isClass)
      return false

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
