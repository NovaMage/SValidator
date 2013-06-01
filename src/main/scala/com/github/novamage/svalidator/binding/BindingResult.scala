package com.github.novamage.svalidator.binding

case class BindingResult[A](errorMessages: List[String], value: Option[A]) {

}
