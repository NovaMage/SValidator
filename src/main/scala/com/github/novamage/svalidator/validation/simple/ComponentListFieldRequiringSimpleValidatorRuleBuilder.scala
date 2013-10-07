package com.github.novamage.svalidator.validation.simple

class ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, B](componentListPropertyExpression: A => List[B], markIndexesOfFieldNameErrors: Boolean) {


  def ForField(fieldName: Symbol) = {
    new ComponentListValidationRuleBuilder[A, B](componentListPropertyExpression, fieldName.name, markIndexesOfFieldNameErrors)
  }

  def ForField(fieldName: String) = {
    new ComponentListValidationRuleBuilder[A, B](componentListPropertyExpression, fieldName, markIndexesOfFieldNameErrors)
  }

}
