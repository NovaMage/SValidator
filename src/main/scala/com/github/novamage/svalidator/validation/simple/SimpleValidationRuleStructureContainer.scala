package com.github.novamage.svalidator.validation.simple

case class SimpleValidationRuleStructureContainer[A, B](validationExpression: (B, A) => Boolean,
                                                        conditionalValidation: Option[A => Boolean],
                                                        errorMessageKey: Option[String],
                                                        errorMessageFormatValues: Option[B => List[Any]],
                                                        metadata: Map[String, List[Any]]) {

}
