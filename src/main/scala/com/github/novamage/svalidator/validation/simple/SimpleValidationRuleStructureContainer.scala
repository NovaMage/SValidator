package com.github.novamage.svalidator.validation.simple

case class SimpleValidationRuleStructureContainer[A, B](validationExpression: (B, A) => Boolean, conditionalValidation: Option[A => Boolean], errorMessageBuilder: Option[(String, B) => String]) {

}
