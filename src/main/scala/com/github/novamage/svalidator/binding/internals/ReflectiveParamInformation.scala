package com.github.novamage.svalidator.binding.internals

import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}

class ReflectiveParamInformation(val parameterName: String,
                                 val binder: TypedBinder[_]) {

}

class JsonReflectiveParamInformation(val parameterName: String,
                                     val binder: JsonTypedBinder[_]) {

}
