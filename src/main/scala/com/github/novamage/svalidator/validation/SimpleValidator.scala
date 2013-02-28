package com.github.novamage.svalidator.validation

abstract class SimpleValidator[T] extends IValidate[T] {

  def buildRules: List[IRuleBuilder[T]]

  override def validate(instance: T) = {
    val rules = buildRules
    val validationFailures = rules.flatMap(_.buildRules).map(_.apply(instance))
    ValidationSummary(validationFailures)
  }

  def For[R](propertyExpression: T => R) = {
    SimpleValidationRuleBuilder[T, R](propertyExpression, Nil)
  }


  case class SimpleValidationRuleBuilder[A, B](propertyExpression: A => B, validationExpressions: List[B => Boolean]) extends IRuleBuilder[A] {

    def Must(ruleExpression: B => Boolean) = {
      SimpleValidationRuleBuilder(propertyExpression, validationExpressions :+ ruleExpression)
    }

    def buildRules = ???
  }

  case class SimpleValidationRule[U](ruleExpression: U => Boolean, fieldName: String, errorMessage: String) extends IValidationRule[U] {
    def apply(instance: U) = if (ruleExpression(instance)) Valid else Invalid(fieldName, errorMessage)
  }

}

