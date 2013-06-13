package com.github.novamage.svalidator.binding

import scala.reflect.runtime.{universe => ru}
import scala.collection.mutable.ListBuffer
import com.github.novamage.svalidator.binding.binders.typed._
import com.github.novamage.svalidator.binding.binders.ITypedBinder
import com.github.novamage.svalidator.binding.binders.special.{EnumerationBinder, RecursiveBinderWrapper, ListBinderWrapper, OptionBinderWrapper}

object TypeBinderRegistry {

  private val binders = ListBuffer[(ITypedBinder[_], ru.TypeTag[_])]()

  def initializeBinders() {
    initializeBinders(BindingConfig.defaultConfig)
  }

  def initializeBinders(config: BindingConfig) {
    binders.clear()
    registerBinder(new StringBinder(config))
    registerBinder(new IntBinder(config))
    registerBinder(new LongBinder(config))
    registerBinder(new FloatBinder(config))
    registerBinder(new DoubleBinder(config))
    registerBinder(new BigDecimalBinder(config))
    registerBinder(new BooleanBinder(config))
    registerBinder(new TimestampBinder(config))
  }

  def clearBinders() {
    binders.clear()
  }

  protected[binding] def getBinderForType(runtimeType: ru.Type, mirror: ru.Mirror): Option[ITypedBinder[_]] = {

    if (runtimeType.asInstanceOf[ru.TypeRef].pre.baseClasses.exists(x => x == ru.typeOf[Enumeration].typeSymbol.asClass)) {
      Some(new EnumerationBinder(runtimeType, mirror))
    }
    else if (runtimeType.erasure =:= ru.typeOf[Option[_]].erasure) {
      getBinderForType(runtimeType.asInstanceOf[ru.TypeRef].args.head, mirror).map(new OptionBinderWrapper(_))
    }
    else if (runtimeType.erasure =:= ru.typeOf[List[_]].erasure) {
      getBinderForType(runtimeType.asInstanceOf[ru.TypeRef].args.head, mirror).map(new ListBinderWrapper(_))
    }
    else {
      binders collectFirst {
        case (binder, tag) if tag.tpe =:= runtimeType => binder
      }
    }


  }

  def registerBinder[A: ru.TypeTag](binder: ITypedBinder[A]) {
    binders.append((binder, ru.typeTag[A]))
  }

  def allowRecursiveBindingForType[A: ru.TypeTag]() {
    binders.append((new RecursiveBinderWrapper[A](), ru.typeTag[A]))
  }

}
