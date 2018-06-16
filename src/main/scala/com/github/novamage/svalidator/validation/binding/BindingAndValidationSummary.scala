package com.github.novamage.svalidator.validation.binding

import com.github.novamage.svalidator.validation.{Localizer, ValidationFailure, ValidationSummary}

import scala.language.implicitConversions

/** Contains information about errors occurred during the binding or validation processes, and the bound instance if
  * at least binding was successful
  *
  * @param validationFailures List of failures that occurred during binding or validation
  * @tparam A Type of the validated instance
  */
sealed abstract class BindingAndValidationSummary[+A](validationFailures: List[ValidationFailure]) extends ValidationSummary(validationFailures) {

  /** Returns the bound and validated instance.  Will only be present if binding was successful.
    */
  def instance: Option[A]

  /** Returns the map of values that was provided when binding was performed
    */
  def valuesMap: Map[String, Seq[String]]

  /** Generates a new summary by applying the given function to this summary's <code>instance</code>
    *
    * @param f Function to apply to the instance
    * @tparam B Type of the validated instance
    */
  def map[B](f: A => B): BindingAndValidationSummary[B] = {
    this match {
      case Success(value) => Success(f(value), valuesMap)
      case Failure(failures) => Failure(failures, valuesMap, instance.map(f))
    }
  }

  /** Generates a new summary by applying the given localizer to the
    * [[com.github.novamage.svalidator.validation.ValidationFailure#localize ValidationFailure.localize]]'s method of all failures
    * contained. If no failures exist, returns this instance
    *
    * @param localizer Localizer to apply to failures
    */
  override def localize(implicit localizer: Localizer): BindingAndValidationSummary[A] = {
    this match {
      case Success(_) => this
      case Failure(failures) => Failure(failures.map(_.localize(localizer)), valuesMap, instance)
    }
  }

}

/** Provides helpers for creating filled and empty summaries
  *
  */
object BindingAndValidationSummary {

  /** Generates an empty summary with no failures, no values map and no instance for the given type parameter
    *
    * @tparam A Type of the instance of the summary
    */
  def empty[A]: BindingAndValidationSummary[A] = Failure(Nil, Map.empty, None)

  /** Generates a summary with the given instance, no failures and no values map
    *
    * @tparam A Type of the instance of the summary
    */
  def filled[A](instance: A): BindingAndValidationSummary[A] = Success(instance, Map.empty)

}

/** Represents summaries that were bound and validated successfully.  It is safely sealed for pattern matching.
  *
  * @param instanceValue Instance that was validated during the validation phase
  * @tparam A Type of the validated instance
  */
sealed class Success[+A] private(val instanceValue: A) extends BindingAndValidationSummary[A](Nil) {

  private var _valuesMap: Map[String, Seq[String]] = _

  def valuesMap: Map[String, Seq[String]] = _valuesMap

  protected[Success] def valuesMap_=(value: Map[String, Seq[String]]): Unit = {
    _valuesMap = value
  }

  def instance: Option[A] = Some(instanceValue)

  override def equals(obj: Any): Boolean = obj match {
    case another: Success[_] => instanceValue.equals(another.instanceValue)
    case _ => false
  }

}


/** Contains convenience methods for alternate ways of building a successful summary
  *
  */
object Success {

  /** Builds a successful summary with the given instance and values map
    *
    * @param instanceValue Instance to put in the summary
    * @param valuesMap     Values map used when binding the instance
    * @tparam A Type of the validated instance
    */
  def apply[A](instanceValue: A, valuesMap: Map[String, Seq[String]]): Success[A] = {
    val result = new Success[A](instanceValue)
    result.valuesMap = valuesMap
    result
  }

  /** Extracts the instance of the given pattern matched summary
    *
    * @param input Success summary to deconstruct
    * @tparam A Type of the validated instance
    */
  def unapply[A](input: Success[A]): Option[A] = input.instance

}

/** Represents summaries failed either binding or validation.  It is safely sealed for pattern matching.
  *
  * @param failures Errors that ocurred during binding or validation of the instance
  * @param instance The instance that was validated, or [[scala.None None]] if binding was unsuccessful
  * @tparam A Type of the validated instance
  */
sealed class Failure[+A] private(failures: List[ValidationFailure], val instance: Option[A]) extends BindingAndValidationSummary[A](failures) {

  private var _valuesMap: Map[String, Seq[String]] = _

  def valuesMap: Map[String, Seq[String]] = _valuesMap

  protected[Failure] def valuesMap_=(value: Map[String, Seq[String]]): Unit = {
    _valuesMap = value
  }

  override def equals(obj: Any): Boolean = obj match {
    case another: Failure[_] => another.validationFailures == failures && instance == another.instance
    case _ => false
  }

}

/** Contains convenience methods for alternate ways of building a failing summary
  *
  */
object Failure {


  /** Builds a failing summary with the given failures, values map and instance option
    *
    * @param failures  Failures that ocurred when validating the instance
    * @param valuesMap Values map used when binding the instance
    * @param instance  Instance that was bound and validated
    * @tparam A Type of the bound and validated instance
    */
  def apply[A](failures: List[ValidationFailure], valuesMap: Map[String, Seq[String]], instance: Option[A]): Failure[A] = {
    val result = new Failure(failures, instance)
    result.valuesMap = valuesMap
    result
  }

  /** Extracts the validation failures of the given pattern matched summary
    *
    * @param input Failure summary to deconstruct
    * @tparam A Type of the validated instance
    */
  def unapply[A](input: Failure[A]): Option[List[ValidationFailure]] = {
    Some(input.validationFailures)
  }

}

