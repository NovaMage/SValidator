package com.github.novamage.svalidator.binding

sealed abstract class BindingResult[A] {

  def isValid: Boolean

  def value: Option[A]

  def fieldErrors: List[FieldError]
}


case class BindingPass[A](private val boundValue: A) extends BindingResult[A] {
  def isValid = true

  def value = Option(boundValue)

  def fieldErrors = Nil
}

case class BindingFailure[A](private val errors: List[FieldError]) extends BindingResult[A] {

  def this(fieldName: String, error: String) = this(List(new FieldError(fieldName, error)))

  def isValid = false

  def value = None

  def fieldErrors = errors
}
