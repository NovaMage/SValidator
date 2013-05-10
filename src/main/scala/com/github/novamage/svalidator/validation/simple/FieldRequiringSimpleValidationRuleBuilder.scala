package com.github.novamage.svalidator.validation.simple


class FieldRequiringSimpleValidationRuleBuilder[A, B](propertyExpression: A => B) {

  def ForField(fieldName: String) = {
    new SimpleValidationRuleBuilder[A, B](propertyExpression, null, List(), fieldName)
  }

  def ForField(fieldName: Symbol): SimpleValidationRuleBuilder[A, B] = {
    ForField(fieldName.name)
  }
}

