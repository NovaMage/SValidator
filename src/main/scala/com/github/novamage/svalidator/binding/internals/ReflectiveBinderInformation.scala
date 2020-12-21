package com.github.novamage.svalidator.binding.internals

import scala.reflect.runtime.{universe => ru}

class ReflectiveBinderInformation(val constructorMirror: ru.MethodMirror,
                                  val paramsInfo: List[ReflectiveParamInformation]) {

}

class JsonReflectiveBinderInformation(val constructorMirror: ru.MethodMirror,
                                      val paramsInfo: List[JsonReflectiveParamInformation]) {

}
