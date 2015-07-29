package com.github.novamage.svalidator.validation.binding

import com.github.novamage.svalidator.validation.{ValidationFailure, ValidationSummary}

sealed abstract class BindingAndValidationSummary[+A](validationFailures: List[ValidationFailure], metadata: Map[String, List[Any]]) extends ValidationSummary(validationFailures, metadata) {

  def instance: Option[A]

  def valuesMap: Map[String, Seq[String]]


}

object BindingAndValidationSummary {

  def empty[A]: BindingAndValidationSummary[A] = Failure(Nil, Map.empty[String, List[Any]], Map.empty[String, Seq[String]], None.asInstanceOf[Option[A]])

  def filled[A](instance: A): BindingAndValidationSummary[A] = Success(instance, Map.empty[String, Seq[String]], Map.empty[String, List[Any]])

}

sealed class Success[+A] private(val instanceValue: A, metadata: Map[String, List[Any]]) extends BindingAndValidationSummary[A](Nil, metadata) {

  private var _valuesMap: Map[String, Seq[String]] = _

  def valuesMap = _valuesMap

  protected[Success] def valuesMap_=(value: Map[String, Seq[String]]): Unit = {
    _valuesMap = value
  }

  def instance: Option[A] = Some(instanceValue)
}

object Success {

  def apply[A](instanceValue: A, valuesMap: Map[String, Seq[String]], metadata: Map[String, List[Any]]): Success[A] = {
    val result = new Success[A](instanceValue, metadata)
    result.valuesMap = valuesMap
    result
  }

  def unapply[A](input: Success[A]): Option[A] = {
    input.instance
  }


}

sealed class Failure[+A] private(failures: List[ValidationFailure], val instance: Option[A], metadata: Map[String, List[Any]]) extends BindingAndValidationSummary[A](failures, metadata) {

  private var _valuesMap: Map[String, Seq[String]] = _

  def valuesMap = _valuesMap

  protected[Failure] def valuesMap_=(value: Map[String, Seq[String]]): Unit = {
    _valuesMap = value
  }

}

object Failure {


  def apply[A](failures: List[ValidationFailure], metadata: Map[String, List[Any]], valuesMap: Map[String, Seq[String]], instance: Option[A]): Failure[A] = {
    val result = new Failure(failures, instance, metadata)
    result.valuesMap = valuesMap
    result
  }

  def unapply[A](input: Failure[A]): Option[(List[ValidationFailure], Map[String, List[Any]])] = {
    Some((input.validationFailures, input.metadata))
  }

}

