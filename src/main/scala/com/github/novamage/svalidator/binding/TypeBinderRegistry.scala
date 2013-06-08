package com.github.novamage.svalidator.binding

import scala.reflect.runtime.{universe => ru}
import scala.collection.mutable.ListBuffer
import com.github.novamage.svalidator.binding.binders.typed.{StringBinder, IntBinder, BooleanBinder}
import com.github.novamage.svalidator.binding.binders.ITypeBinder
import com.github.novamage.svalidator.binding.binders.special.{ListBinderWrapper, OptionBinderWrapper}

object TypeBinderRegistry {

  private val binders = ListBuffer[(ITypeBinder[_], ru.TypeTag[_])]()

  {
    registerBinder(new StringBinder)
    registerBinder(new IntBinder)
    registerBinder(new BooleanBinder)
  }

  def getBinderForType(typeTag: ru.Type): Option[ITypeBinder[_]] = {
    if (typeTag.erasure == ru.typeOf[Option[Any]].erasure) {
      binders collectFirst {
        case (binder, tag) if tag.tpe == typeTag.asInstanceOf[ru.TypeRef].args.head => new OptionBinderWrapper(binder)
      }
    }
    else if (typeTag.erasure == ru.typeOf[List[Any]].erasure) {
      binders collectFirst {
        case (binder, tag) if tag.tpe == typeTag.asInstanceOf[ru.TypeRef].args.head => new ListBinderWrapper(binder)
      }
    }
    else {
      binders collectFirst {
        case (binder, tag) if tag.tpe == typeTag => binder
      }
    }


  }


  def registerBinder[T: ru.TypeTag](binder: ITypeBinder[T]) {
    binders.append((binder, ru.typeTag[T]))
  }

}
