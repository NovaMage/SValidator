package com.github.novamage.svalidator

import com.github.novamage.svalidator.validation.ValidationSummary

package object testing {

  implicit def ShouldExtensions(validationSummary: ValidationSummary): ShouldExtensions = new ShouldExtensions(validationSummary)
}
