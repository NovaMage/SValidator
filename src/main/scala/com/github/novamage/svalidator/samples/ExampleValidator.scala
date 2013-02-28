package com.github.novamage.svalidator.samples

import com.github.novamage.svalidator.validation.SimpleValidator

class ExampleValidator extends SimpleValidator[ExampleValidatedClass]{

  def buildRules = For(_.age).Must(_ > 18) :: Nil
}
