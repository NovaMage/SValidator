package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.{ValidationFailure, IValidationRule, IRuleBuilder, IValidate}

class ComponentValidationRuleBuilder[A, B](componentPropertyExpression: A => B, fieldName: String) {

  def validateUsing(validator: IValidate[B]): IRuleBuilder[A] = {
    new ComponentValidationWrapper[A, B](componentPropertyExpression, fieldName, validator)
  }


}

private class ComponentValidationWrapper[A, B](componentPropertyExpression: A => B, fieldName: String, componentValidator: IValidate[B]) extends IRuleBuilder[A] {

  protected[validation] def buildRules(instance: A): RuleStreamCollection[A] = {
    RuleStreamCollection(List(Stream(new ComponentValidationRule[A, B](componentPropertyExpression, fieldName, componentValidator))))
  }
}

private class ComponentValidationRule[A, B](componentPropertyExpression: A => B, fieldName: String, componentValidator: IValidate[B]) extends IValidationRule[A] {

  def apply(instance: A): List[ValidationFailure] = {
    val component = componentPropertyExpression.apply(instance)
    componentValidator.validate(component).validationFailures.map(x => x.copy(fieldName = fieldName + "." + x.fieldName))
  }
}
