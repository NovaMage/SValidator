package com.github.novamage.svalidator.validation.simple

//A is the type of the whole validated instance
//B is the type of the properly directly extracted
//C is the direct type used on validation rules (varies according if it's a list, an option, etc)

abstract class AbstractValidationRuleBuilder[A, B, C](propertyExpression: A => B,
                                                      currentRuleStructure: SimpleValidationRuleStructureContainer[A, C],
                                                      validationExpressions: List[SimpleValidationRuleStructureContainer[A, C]],
                                                      fieldName: String) extends IRuleBuilder[A] {


}
