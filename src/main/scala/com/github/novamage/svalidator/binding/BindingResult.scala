package com.github.novamage.svalidator.binding

sealed abstract class BindingResult[A] {

  def isValid: Boolean

  def value: Option[A]

  def fieldErrors: List[FieldError]
}


case class BindingPass[A](private val boundValue: A) extends BindingResult[A] {

  override def isValid = true

  override def value = Option(boundValue)

  override def fieldErrors: List[FieldError] = Nil

}

case class BindingFailure[A](private val errors: List[FieldError], cause: Option[Throwable]) extends BindingResult[A] {

  def this(fieldName: String, error: String, cause: Option[Throwable]) = this(List(FieldError(fieldName, error)), cause)

  override def isValid = false

  override def value: Option[A] = None

  override def fieldErrors: List[FieldError] = errors

}
