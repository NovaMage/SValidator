package com.github.novamage.svalidator.validation.simple

case class SimpleValidationRuleStructureContainer[A, B](validationExpression: B => Boolean, conditionalValidation: Option[A => Boolean], errorMessageBuilder: Option[(String, B) => String]) {

}
