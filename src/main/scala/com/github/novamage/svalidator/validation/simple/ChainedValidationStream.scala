package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.IValidationRule

case class ChainedValidationStream[-A](mainStream: List[Stream[IValidationRule[A]]],
                                      dependsOnUpstream: Option[RuleStreamCollection[A]]) {

}
