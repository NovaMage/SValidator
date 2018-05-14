package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation._

//import scala.collection.mutable

class ComponentListValidationRuleBuilder[A, B](componentListPropertyExpression: A => List[B], fieldName: String, markIndexesOfFieldNameErrors: Boolean) {

  def validateUsing(validator: IValidate[B]): IRuleBuilder[A] = {
    new ComponentListValidationWrapper[A, B](componentListPropertyExpression, fieldName, validator, markIndexesOfFieldNameErrors)
  }
}

private class ComponentListValidationWrapper[A, B](componentListPropertyExpression: A => List[B], fieldName: String, componentValidator: IValidate[B], markIndexesOfFieldNameErrors: Boolean) extends IRuleBuilder[A] {

  protected[validation] def buildRules(instance: A): RuleStreamCollection[A] = {
    RuleStreamCollection(List(ChainedValidationStream(
      List(Stream(new ComponentListValidationRule[A, B](
        componentListPropertyExpression,
        fieldName,
        componentValidator,
        markIndexesOfFieldNameErrors))), None)))
  }
}

private class ComponentListValidationRule[A, B](componentListPropertyExpression: A => List[B], fieldName: String, componentValidator: IValidate[B], markIndexesOfFieldNameErrors: Boolean) extends IValidationRule[A] {

  def apply(instance: A, localizer: Localizer): List[ValidationFailure] = {
    val components = componentListPropertyExpression.apply(instance)
    val validationResults = components.zipWithIndex.flatMap {
      case (component, index) =>
        //We pass in the identity localizer here, and let the top level validator handle the localization
        val summary = componentValidator.validate(component, (x: String) => x)
        val indexInfo = if (markIndexesOfFieldNameErrors) "[" + index + "]" else Constants.emptyString
        summary.validationFailures.map(x => x.copy(fieldName + indexInfo + "." + x.fieldName))

    }
    validationResults
  }
}
