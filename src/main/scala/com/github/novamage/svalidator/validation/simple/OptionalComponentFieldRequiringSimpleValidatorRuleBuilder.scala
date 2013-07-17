package com.github.novamage.svalidator.validation.simple

class OptionalComponentFieldRequiringSimpleValidatorRuleBuilder[A, B](optionalComponentPropertyExpression: A => Option[B]) {

  def ForField(fieldName: Symbol) = {
    new ComponentOptionValidationRuleBuilder[A, B](optionalComponentPropertyExpression, fieldName.name)
  }

  def ForField(fieldName: String) = {
    new ComponentOptionValidationRuleBuilder[A, B](optionalComponentPropertyExpression, fieldName)
  }

}
