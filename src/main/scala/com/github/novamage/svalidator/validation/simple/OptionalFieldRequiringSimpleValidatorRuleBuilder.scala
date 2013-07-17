package com.github.novamage.svalidator.validation.simple

class OptionalFieldRequiringSimpleValidatorRuleBuilder[A,B](optionalPropertyExpression: A => Option[B]) {

  def ForField(fieldName: String): SimpleValidationRuleBuilder[A, B] = {
    new SimpleOptionValidationRuleBuilder[A, B](optionalPropertyExpression, null, List(), fieldName)
  }

  def ForField(fieldName: Symbol): SimpleValidationRuleBuilder[A, B] = {
    new SimpleOptionValidationRuleBuilder[A, B](optionalPropertyExpression, null, List(), fieldName.name)
  }

}
