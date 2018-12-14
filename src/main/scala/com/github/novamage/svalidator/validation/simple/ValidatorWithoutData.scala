package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.simple.internals.RuleBuilder
import com.github.novamage.svalidator.validation.{ValidationSummary, Validator}

trait ValidatorWithoutData[A] extends Validator[A, Nothing] {
  this: SimpleValidatorWithData[A, Nothing] =>

  override def validate(implicit instance: A): ValidationSummary

  override def WithRules(ruleBuilders: RuleBuilder[A]*)(implicit instance: A): ValidationSummary = {
    ValidationSummary(WithRulesAndData(None, ruleBuilders: _*).validationFailures)
  }

}
