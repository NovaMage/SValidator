package integration.com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.simple.SimpleValidator
import com.github.novamage.svalidator.validation.simple.constructs._

class PersonValidator extends SimpleValidator[Person] {

  override def buildRules = List(
    For { _.firstName } ForField "firstName"
      mustNot be empty (),
    For { _.lastName } ForField "lastName"
      mustNot be empty ()
    )
}
