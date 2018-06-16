package com.github.novamage.svalidator.validation.simple

/** Chain builder that requires providing a field name for the generation of error messages further on
  *
  * @tparam A Type of the instance being validated
  * @tparam B Type of the extracted property being validated
  */
class FieldListRequiringSimpleValidatorRuleBuilder[A, B](propertyListExpression: A => List[B], markIndexesOfErrors: Boolean) {

  /** Applies the given string as the field name for any error messages generated during this chain builder.
    *
    * @param fieldName Field name to use for error messages
    */
  def ForField(fieldName: String): SimpleListValidationRuleStarterBuilder[A, B, Nothing] = {
    new SimpleListValidationRuleStarterBuilder(propertyListExpression, None, Nil, fieldName, markIndexesOfErrors, None, None, None)
  }

  /** Applies the name of the given [[scala.Symbol Symbol]] as the field name for any error messages generated during this chain builder.
    *
    * @param fieldName Field name to use for error messages
    */
  def ForField(fieldName: Symbol): SimpleListValidationRuleStarterBuilder[A, B, Nothing] = {
    new SimpleListValidationRuleStarterBuilder[A, B, Nothing](propertyListExpression, None, Nil, fieldName.name, markIndexesOfErrors, None, None, None)
  }

}
