package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.{ValidationFailure, IValidationRule, IValidate, IRuleBuilder}

class ComponentOptionValidationRuleBuilder[A, B](componentOptionPropertyExpression: A => Option[B], fieldName: String) {

  def validateUsing(validator: IValidate[B]): IRuleBuilder[A] = {
    new ComponentOptionValidationWrapper[A, B](componentOptionPropertyExpression, fieldName, validator)
  }


}

private class ComponentOptionValidationWrapper[A, B](componentOptionPropertyExpression: A => Option[B], fieldName: String, componentValidator: IValidate[B]) extends IRuleBuilder[A] {

  protected[validation] def buildRules(instance: A): RuleStreamCollection[A] = {
    RuleStreamCollection(List(Stream(new ComponentOptionValidationRule[A, B](componentOptionPropertyExpression, fieldName, componentValidator))))
  }
}

private class ComponentOptionValidationRule[A, B](componentPropertyExpression: A => Option[B], fieldName: String, componentValidator: IValidate[B]) extends IValidationRule[A] {

  def apply(instance: A): List[ValidationFailure] = {
    val componentOption = componentPropertyExpression.apply(instance)
    componentOption.toList.map(componentValidator.validate).flatMap(_.validationFailures.map(x => x.copy(fieldName = fieldName + "." + x.fieldName)))
  }
}

