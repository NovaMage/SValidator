package com.github.novamage.svalidator.binding

import scala.reflect.runtime.{universe => ru}
import scala.collection.mutable.ListBuffer

object TypeBinderRegistry {

  private val binders = ListBuffer[(ITypeBinder[_], ru.TypeTag[_])]()

  {
    registerBinder(new StringBinder)
    registerBinder(new IntBinder)
    registerBinder(new BooleanBinder)
  }

  def getBinderForType(runtimeType: ru.Type): Option[ITypeBinder[_]] = {
    binders.find {
      case (binder, typeTag) => typeTag.tpe == runtimeType
    } map {
      case (binder, typeTag) => binder
    }
  }


  def registerBinder[T: ru.TypeTag](binder: ITypeBinder[T]) {
    binders.append((binder, ru.typeTag[T]))
  }

}
