package com.github.novamage.svalidator.validation.simple

case class RuleStreamCollection[-A](chains: List[ChainedValidationStream[A]]) {


}


object RuleStreamCollection {

  val Empty = RuleStreamCollection(Nil)
}
