package integration.com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.simple.SimpleValidator
import com.github.novamage.svalidator.validation.IRuleBuilder
import com.github.novamage.svalidator.validation.simple.constructs._

class PersonValidator extends SimpleValidator[Person] {

  override def buildRules: List[IRuleBuilder[Person]] = List(

    For {_.firstName} ForField 'firstName
      mustNot be empty() withMessage "First name is required"
      must have maxLength 32 withMessage "Must have 32 characters or less",

    For {_.lastName} ForField 'lastName
      mustNot be empty() withMessage "Last name is required"
      must have maxLength 32 withMessage "Must have 32 characters or less",

    For {_.age} ForField 'age
      mustNot be negative() withMessage "Must be a positive number",

    For {_.married} ForField 'married
      must be(false) when {_.age < 18} withMessage "Must be 18 years or older to allow marking marriage",

    For {_.hasJob} ForField 'hasJob
      must be(false) when {_.age < 21} withMessage "Must be 21 years or older to allow marking a job"

  )
}
