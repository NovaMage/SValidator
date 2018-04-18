package com.github.novamage.svalidator.validation.simple

class FieldListRequiringSimpleValidatorRuleBuilder[A, B](propertyListExpression: A => List[B], markIndexesOfErrors: Boolean) {

  def ForField(fieldName: String): SimpleListValidationRuleStarterBuilder[A, B] = {
    new SimpleListValidationRuleStarterBuilder[A, B](propertyListExpression, None, Nil, fieldName, markIndexesOfErrors, None)
  }

  def ForField(fieldName: Symbol): SimpleListValidationRuleStarterBuilder[A, B] = {
    new SimpleListValidationRuleStarterBuilder[A, B](propertyListExpression, None, Nil, fieldName.name, markIndexesOfErrors, None)
  }

}
