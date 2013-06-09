package com.github.novamage.svalidator.validation.binding

import com.github.novamage.svalidator.validation.{ValidationFailure, ValidationSummary}

case class BindingAndValidationSummary[A](private val failures: List[ValidationFailure], instance: Option[A]) extends ValidationSummary(failures)
