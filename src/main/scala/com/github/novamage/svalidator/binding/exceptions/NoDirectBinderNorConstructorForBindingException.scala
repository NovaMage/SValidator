package com.github.novamage.svalidator.binding.exceptions

import scala.reflect.runtime.{universe => ru}

/** Thrown when a type that is attempted to bind doesn't have any usable primary constructors (possibly because all
  * constructors are private or otherwise inaccessible) and therefore can not be bound unless a direct binder is provided
  * for it.
  *
  * @param scalaType Type of the value attempted to bind
  */
class NoDirectBinderNorConstructorForBindingException(scalaType: ru.Type) extends Exception(
  s"The type $scalaType does not have an accessible/usable primary constructor, and no direct binder exists for it") {

}
