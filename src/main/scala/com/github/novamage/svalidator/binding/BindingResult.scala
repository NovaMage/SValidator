package com.github.novamage.svalidator.binding

import com.github.novamage.svalidator.validation.{Localizer, MessageParts}

sealed abstract class BindingResult[+A] {

  def isValid: Boolean

  def value: Option[A]

  def fieldErrors: List[FieldError]

  def localized(implicit localizer: Localizer): BindingResult[A]
}


case class BindingPass[+A](private val boundValue: A) extends BindingResult[A] {

  override def isValid = true

  override def value = Option(boundValue)

  override def fieldErrors: List[FieldError] = Nil

  override def localized(implicit localizer: Localizer): BindingResult[A] = this
}

case class BindingFailure[+A](private val errors: List[FieldError], cause: Option[Throwable]) extends BindingResult[A] {


  override def isValid = false

  override def value: Option[A] = None

  override def fieldErrors: List[FieldError] = errors

  override def localized(implicit localizer: Localizer): BindingResult[A] = BindingFailure(errors.map(_.localize), cause)

}

object BindingFailure {

  def apply(fieldName: String, error: String, cause: Option[Throwable]): BindingFailure[Nothing] = BindingFailure(List(FieldError(fieldName, MessageParts(error))), cause)

  def apply(fieldName: String, error: MessageParts, cause: Option[Throwable]): BindingFailure[Nothing] = BindingFailure(List(FieldError(fieldName, error)), cause)

}
