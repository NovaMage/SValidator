package integration.com.github.novamage.svalidator.validation.binding

import testUtils.Observes
import com.github.novamage.svalidator.validation.binding.BindingValidator

class BindingValidatorSpecs extends Observes {

  class ATestingClass(aString: String, anInt: Int, aFloat: Float, aDecimal: BigDecimal, anOptionalDouble: Option[Double], anOptionalString: Option[String])


  class ATestingClassValidator extends BindingValidator[ATestingClass]{
    def buildRules = ???
  }

}
