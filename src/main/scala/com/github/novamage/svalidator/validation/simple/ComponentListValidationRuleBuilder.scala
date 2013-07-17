package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation._

class ComponentListValidationRuleBuilder[A, B](componentListPropertyExpression: A => List[B], fieldName: String) {

  def validateUsing(validator: IValidate[B]): IRuleBuilder[A] = {
    new ComponentListValidationWrapper[A, B](componentListPropertyExpression, fieldName, validator)
  }
}

private class ComponentListValidationWrapper[A, B](componentListPropertyExpression: A => List[B], fieldName: String, componentValidator: IValidate[B]) extends IRuleBuilder[A] {

  protected[validation] def buildRules: List[IValidationRule[A]] = {
    List(new ComponentListValidationRule[A, B](componentListPropertyExpression, fieldName, componentValidator))
  }
}

private class ComponentListValidationRule[A, B](componentListPropertyExpression: A => List[B], fieldName: String, componentValidator: IValidate[B]) extends IValidationRule[A] {

  def apply(instance: A): List[ValidationFailure] = {
    val components = componentListPropertyExpression.apply(instance)
    val validationResults = components.zipWithIndex.flatMap {
      case (component, index) => {
        val summary = componentValidator.validate(component)
        summary.validationFailures.map(x => x.copy(fieldName + "[" + index + "]." + x.fieldName))
      }
    }
    validationResults
  }
}
