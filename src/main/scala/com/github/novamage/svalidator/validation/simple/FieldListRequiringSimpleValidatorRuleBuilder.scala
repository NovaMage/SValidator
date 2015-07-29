package com.github.novamage.svalidator.validation.simple

class FieldListRequiringSimpleValidatorRuleBuilder[A, B](propertyListExpression: A => List[B], markIndexesOfErrors: Boolean) {

  def ForField(fieldName: String): SimpleListValidationRuleBuilder[A, B] = {
    new SimpleListValidationRuleBuilder[A, B](propertyListExpression, null, List(), fieldName, markIndexesOfErrors, Map.empty[String, List[Any]])
  }

  def ForField(fieldName: Symbol): SimpleListValidationRuleBuilder[A, B] = {
    new SimpleListValidationRuleBuilder[A, B](propertyListExpression, null, List(), fieldName.name, markIndexesOfErrors, Map.empty[String, List[Any]])
  }

}
