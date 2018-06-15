package com.github.novamage.svalidator.validation.simple

class ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, B](componentListPropertyExpression: A => List[B], markIndexesOfFieldNameErrors: Boolean) {


  def ForField(fieldName: Symbol): ComponentListValidationRuleBuilder[A, B] = {
    new ComponentListValidationRuleBuilder[A, B](componentListPropertyExpression, fieldName.name, markIndexesOfFieldNameErrors)
  }

  def ForField(fieldName: String): ComponentListValidationRuleBuilder[A, B] = {
    new ComponentListValidationRuleBuilder[A, B](componentListPropertyExpression, fieldName, markIndexesOfFieldNameErrors)
  }

}
