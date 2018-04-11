package com.github.novamage.svalidator.validation

case class ValidationFailure(fieldName: String,
                             message: String,
                             metadata: Map[String, List[Any]])

