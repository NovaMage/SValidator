package com.github.novamage.svalidator.validation.simple

class FieldListRequiringSimpleValidatorRuleBuilder[A, B](propertyListExpression: A => List[B], markIndexesOfErrors: Boolean) {

  def ForField(fieldName: String): AbstractValidationRuleBuilder[A, List[B], B] = {
    new SimpleListValidationRuleBuilder[A, B](propertyListExpression, null, List(), fieldName, markIndexesOfErrors)
  }

  def ForField(fieldName: Symbol): AbstractValidationRuleBuilder[A, List[B], B] = {
    new SimpleListValidationRuleBuilder[A, B](propertyListExpression, null, List(), fieldName.name, markIndexesOfErrors)
  }

}
