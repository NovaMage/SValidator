package com.github.novamage.svalidator.binding

import scala.reflect.runtime.{universe => ru}
import scala.collection.mutable.ListBuffer
import com.github.novamage.svalidator.binding.binders.typed._
import com.github.novamage.svalidator.binding.binders.ITypeBinder
import com.github.novamage.svalidator.binding.binders.special.{ListBinderWrapper, OptionBinderWrapper}

object TypeBinderRegistry {

  private val binders = ListBuffer[(ITypeBinder[_], ru.TypeTag[_])]()

  def initializeBinders() {
    initializeBinders(BindingConfig.defaultConfig)
  }

  def initializeBinders(config: BindingConfig) {
    registerBinder(new StringBinder(config))
    registerBinder(new IntBinder(config))
    registerBinder(new LongBinder(config))
    registerBinder(new BooleanBinder(config))
    registerBinder(new TimestampBinder(config))
  }


  def getBinderForType(typeTag: ru.Type): Option[ITypeBinder[_]] = {
    if (typeTag.erasure == ru.typeOf[Option[Any]].erasure) {
      binders collectFirst {
        case (binder, tag) if tag.tpe =:= typeTag.asInstanceOf[ru.TypeRef].args.head => new OptionBinderWrapper(binder)
      }
    }
    else if (typeTag.erasure == ru.typeOf[List[Any]].erasure) {
      binders collectFirst {
        case (binder, tag) if tag.tpe =:= typeTag.asInstanceOf[ru.TypeRef].args.head => new ListBinderWrapper(binder)
      }
    }
    else {
      binders collectFirst {
        case (binder, tag) if tag.tpe =:= typeTag => binder
      }
    }


  }


  def registerBinder[T: ru.TypeTag](binder: ITypeBinder[T]) {
    binders.append((binder, ru.typeTag[T]))
  }

}
