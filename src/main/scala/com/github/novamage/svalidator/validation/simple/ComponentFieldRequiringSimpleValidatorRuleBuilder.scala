package com.github.novamage.svalidator.validation.simple

class ComponentFieldRequiringSimpleValidatorRuleBuilder[A, B](componentPropertyExpression: A => B) {

  def ForField(fieldName: Symbol) = {
    new ComponentValidationRuleBuilder[A, B](componentPropertyExpression, fieldName.name)
  }

  def ForField(fieldName: String) = {
    new ComponentValidationRuleBuilder[A, B](componentPropertyExpression, fieldName)
  }

}
