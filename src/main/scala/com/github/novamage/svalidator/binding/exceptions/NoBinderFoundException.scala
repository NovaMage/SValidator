package com.github.novamage.svalidator.binding.exceptions

import scala.reflect.runtime.{universe => ru}

/** This exception is thrown when
  * [[com.github.novamage.svalidator.binding.binders.special.MapToObjectBinder MapToObjectBinder]] can't find a binder
  * for a type in the constructor of the type it is trying to bind and
  * [[com.github.novamage.svalidator.binding.TypeBinderRegistry#allowRecursiveBindingForType TypeBinderRegistry.allowRecursiveBindingForType]]
  * hasn't been enabled for the type.
  *
  * @param scalaType Type whose binder was not found and reflective binding has not been enabled
  */
class NoBinderFoundException(scalaType: ru.Type) extends Exception(s"No binder found for type: $scalaType. To allow " +
  s"recursively binding into this type, call TypeBinderRegistry.allowRecursiveBindingForType once in your initialization code " +
  s"for the given type.") {
}
