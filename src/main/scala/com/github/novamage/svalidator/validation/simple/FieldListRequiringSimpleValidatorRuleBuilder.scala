package com.github.novamage.svalidator.validation.simple

class FieldListRequiringSimpleValidatorRuleBuilder[A, B](propertyListExpression: A => List[B], markIndexesOfErrors: Boolean) {

  def ForField(fieldName: String): SimpleListValidationRuleStarterBuilder[A, B, Nothing] = {
    new SimpleListValidationRuleStarterBuilder(propertyListExpression, None, Nil, fieldName, markIndexesOfErrors, None, None, None)
  }

  def ForField(fieldName: Symbol): SimpleListValidationRuleStarterBuilder[A, B, Nothing] = {
    new SimpleListValidationRuleStarterBuilder[A, B, Nothing](propertyListExpression, None, Nil, fieldName.name, markIndexesOfErrors, None, None, None)
  }

}
