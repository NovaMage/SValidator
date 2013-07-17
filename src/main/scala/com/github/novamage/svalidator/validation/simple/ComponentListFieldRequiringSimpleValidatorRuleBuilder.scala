package com.github.novamage.svalidator.validation.simple

class ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, B](componentListPropertyExpression: A => List[B]) {


  def ForField(fieldName: Symbol) = {
    new ComponentListValidationRuleBuilder[A, B](componentListPropertyExpression, fieldName.name)
  }

  def ForField(fieldName: String) = {
    new ComponentListValidationRuleBuilder[A, B](componentListPropertyExpression, fieldName)
  }

}
