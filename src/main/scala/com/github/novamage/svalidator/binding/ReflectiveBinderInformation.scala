package com.github.novamage.svalidator.binding

import scala.reflect.runtime.{universe => ru}

class ReflectiveBinderInformation(val constructorMirror: ru.MethodMirror,
                                  val paramsInfo: List[ReflectiveParamInformation]) {

}
