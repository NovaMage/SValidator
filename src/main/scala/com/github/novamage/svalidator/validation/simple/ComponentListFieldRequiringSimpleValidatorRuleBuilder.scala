package com.github.novamage.svalidator.validation.simple

/** Chain builder that requires providing a field name for the generation of error messages further on
  *
  * @tparam A Type of the instance being validated
  * @tparam B Type of the extracted component property being validated
  */
class ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, B](componentListPropertyExpression: A => List[B], markIndexesOfFieldNameErrors: Boolean) {


  /** Applies the given string as the field name for any error messages generated during this chain builder.
    *
    * @param fieldName Field name to use for error messages
    */
  def ForField(fieldName: String): ComponentListValidationRuleBuilder[A, B] = {
    new ComponentListValidationRuleBuilder[A, B](componentListPropertyExpression, fieldName, markIndexesOfFieldNameErrors)
  }

  /** Applies the name of the given [[scala.Symbol Symbol]] as the field name for any error messages generated during this chain builder.
    *
    * @param fieldName Field name to use for error messages
    */
  def ForField(fieldName: Symbol): ComponentListValidationRuleBuilder[A, B] = {
    new ComponentListValidationRuleBuilder[A, B](componentListPropertyExpression, fieldName.name, markIndexesOfFieldNameErrors)
  }

}
