package com.github.novamage.svalidator.binding.exceptions

import scala.reflect.runtime.{universe => ru}

class NoDirectBinderNorConstructorForBindingException(scalaType: ru.Type) extends Exception(
  s"The type $scalaType does not have a usable primary constructor, and no direct binder exists for it") {

}
