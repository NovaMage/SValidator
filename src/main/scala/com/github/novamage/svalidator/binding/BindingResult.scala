package com.github.novamage.svalidator.binding

import com.github.novamage.svalidator.validation.{Localizer, MessageParts}

/** Contains information about the result of a binding process
  *
  * @tparam A Type of the object that was attempted to bind
  */
sealed abstract class BindingResult[+A] {

  /** Returns true if binding was successful
    */
  def isValid: Boolean

  /** Returns the bound value if binding was successful, [[scala.None None]] otherwise
    */
  def value: Option[A]

  /** Returns the list of field errors in case of binding failure
    */
  def fieldErrors: List[FieldError]

  /** Applies the given [[com.github.novamage.svalidator.validation.Localizer Localizer]] to all field errors in this result
    *
    * @param localizer Localizer to apply to field errors
    * @return A new binding result with all field errors localized using the given localizer
    */
  def localized(implicit localizer: Localizer): BindingResult[A]
}

/** Represents binding results that were bound successfully.  It is safely sealed for pattern matching.
  *
  * @param boundValue Value that was bound
  * @tparam A Type of the bound value
  */
case class BindingPass[+A](private val boundValue: A) extends BindingResult[A] {

  override def isValid = true

  override def value = Option(boundValue)

  override def fieldErrors: List[FieldError] = Nil

  override def localized(implicit localizer: Localizer): BindingResult[A] = this
}

/** Represents binding results that failed binding.  It is safely sealed for pattern matching.
  *
  * @param errors Errors that occurred during binding
  * @param cause The exception that was thrown and caused the binding to fail, if any
  * @tparam A Type of the value that was attempted to bind
  */
case class BindingFailure[+A](private val errors: List[FieldError], cause: Option[Throwable]) extends BindingResult[A] {


  override def isValid = false

  override def value: Option[A] = None

  override def fieldErrors: List[FieldError] = errors

  override def localized(implicit localizer: Localizer): BindingResult[A] = BindingFailure(errors.map(_.localize), cause)

}

/** Convenience methods for alternate ways of building a [[com.github.novamage.svalidator.binding.BindingFailure BindingFailure]]
  */
object BindingFailure {

  def apply(fieldName: String, error: String, cause: Option[Throwable]): BindingFailure[Nothing] = BindingFailure(List(FieldError(fieldName, MessageParts(error))), cause)

  def apply(fieldName: String, error: MessageParts, cause: Option[Throwable]): BindingFailure[Nothing] = BindingFailure(List(FieldError(fieldName, error)), cause)

}
