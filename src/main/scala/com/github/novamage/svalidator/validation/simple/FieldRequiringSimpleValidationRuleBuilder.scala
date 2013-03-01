package com.github.novamage.svalidator.validation.simple

class FieldRequiringSimpleValidationRuleBuilder[A, B](propertyExpression: A => B, validationExpressions: List[B => Boolean], errorMessages: List[(String, B) => String]) {

  def ForField(fieldName: String) = {
    new SimpleValidationRuleBuilder[A, B](propertyExpression, validationExpressions, fieldName, errorMessages, x => true)
  }
}

