package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.IValidationRule

case class RuleStreamCollection[-A](ruleStreams: List[Stream[IValidationRule[A]]]) {

}


object RuleStreamCollection {

  def empty[A]: RuleStreamCollection[A] = RuleStreamCollection[A](Nil)
}
