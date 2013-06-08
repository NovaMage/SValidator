package com.github.novamage.svalidator.binding.exceptions

import scala.reflect.runtime.{universe => ru}

class NoBinderFoundException(scalaType: ru.Type) extends Exception("No binder found for type: " + scalaType) {
}
