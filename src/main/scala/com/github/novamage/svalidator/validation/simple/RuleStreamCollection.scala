package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.IValidationRule

case class RuleStreamCollection[-A](ruleStreams: List[Stream[IValidationRule[A]]], metadata: Map[String, List[Any]]) {

}


object RuleStreamCollection {

  val Empty = RuleStreamCollection(Nil, Map.empty[String, List[Any]])
}
