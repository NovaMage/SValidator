package com.github.novamage.svalidator.validation.simple.internals

case class ChainedValidationStream[-A](mainStream: List[Stream[IValidationRule[A]]],
                                      dependsOnUpstream: Option[RuleStreamCollection[A]]) {

}
