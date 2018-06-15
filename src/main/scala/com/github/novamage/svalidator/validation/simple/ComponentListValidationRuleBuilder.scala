package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation._
import com.github.novamage.svalidator.validation.simple.internals.{ChainedValidationStream, IValidationRule, RuleBuilder, RuleStreamCollection}

//import scala.collection.mutable

class ComponentListValidationRuleBuilder[A, B](componentListPropertyExpression: A => List[B], fieldName: String, markIndexesOfFieldNameErrors: Boolean) {

  def validateUsing(validator: Validator[B]): RuleBuilder[A] = {
    new ComponentListValidationWrapper[A, B](componentListPropertyExpression, fieldName, validator, markIndexesOfFieldNameErrors)
  }
}

private class ComponentListValidationWrapper[A, B](componentListPropertyExpression: A => List[B], fieldName: String, componentValidator: Validator[B], markIndexesOfFieldNameErrors: Boolean) extends RuleBuilder[A] {

  protected[validation] def buildRules(instance: A): RuleStreamCollection[A] = {
    RuleStreamCollection(List(ChainedValidationStream(
      List(Stream(new ComponentListValidationRule[A, B](
        componentListPropertyExpression,
        fieldName,
        componentValidator,
        markIndexesOfFieldNameErrors))), None)))
  }
}

private class ComponentListValidationRule[A, B](componentListPropertyExpression: A => List[B], fieldName: String, componentValidator: Validator[B], markIndexesOfFieldNameErrors: Boolean) extends IValidationRule[A] {

  def apply(instance: A): List[ValidationFailure] = {
    val components = componentListPropertyExpression.apply(instance)
    val validationResults = components.zipWithIndex.flatMap {
      case (component, index) =>
        val summary = componentValidator.validate(component)
        val indexInfo = if (markIndexesOfFieldNameErrors) "[" + index + "]" else ""
        summary.validationFailures.map(x => x.copy(fieldName + indexInfo + "." + x.fieldName))
    }
    validationResults
  }
}
