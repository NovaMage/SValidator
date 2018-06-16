package com.github.novamage.svalidator

import com.github.novamage.svalidator.validation.ValidationSummary
import scala.language.implicitConversions

package object testing {

  /** Provides extensions for testing validation summaries generated by validators
    */
  implicit def ShouldExtensions(validationSummary: ValidationSummary): ShouldExtensions = new ShouldExtensions(validationSummary)
}
