package com.github.novamage.svalidator.binding

sealed case class FieldError(fieldName: String, errorMessage: String)

