package com.github.novamage.svalidator.validation.simple

class FieldListRequiringSimpleValidatorRuleBuilder[A, B](propertyListExpression: A => List[B], markIndexesOfErrors: Boolean) {

  def ForField(fieldName: String): SimpleListValidationRuleStarterBuilder[A, B] = {
    new SimpleListValidationRuleStarterBuilder[A, B](propertyListExpression, null, Nil, fieldName, markIndexesOfErrors)
  }

  def ForField(fieldName: Symbol): SimpleListValidationRuleStarterBuilder[A, B] = {
    new SimpleListValidationRuleStarterBuilder[A, B](propertyListExpression, null, Nil, fieldName.name, markIndexesOfErrors)
  }

}
