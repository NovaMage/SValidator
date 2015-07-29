package com.github.novamage.svalidator.validation.binding

import com.github.novamage.svalidator.validation.{ValidationFailure, ValidationSummary}

sealed abstract class BindingAndValidationSummary[+A](validationFailures: List[ValidationFailure], metadata: Map[String, List[Any]]) extends ValidationSummary(validationFailures, metadata) {

  def instance: Option[A]

  def valuesMap: Map[String, Seq[String]]


}

object BindingAndValidationSummary {

  def empty[A]: BindingAndValidationSummary[A] = Failure(Nil, Map.empty[String, Seq[String]], None.asInstanceOf[Option[A]], Map.empty[String, List[Any]])

  def filled[A](instance: A): BindingAndValidationSummary[A] = Success(instance, Map.empty[String, Seq[String]], Map.empty[String, List[Any]])

}

sealed case class Success[+A] private(_metadata: Map[String, List[Any]], instanceValue: A) extends BindingAndValidationSummary[A](Nil, _metadata) {

  private var _valuesMap: Map[String, Seq[String]] = _

  def valuesMap = _valuesMap

  protected[Success] def valuesMap_=(value: Map[String, Seq[String]]): Unit = {
    _valuesMap = value
  }

  def instance: Option[A] = Some(instanceValue)
}

object Success {

  def apply[A](instanceValue: A, valuesMap: Map[String, Seq[String]], metadata: Map[String, List[Any]]): Success[A] = {
    val result = new Success[A](metadata, instanceValue)
    result.valuesMap = valuesMap
    result
  }
}

sealed case class Failure[+A] private(failures: List[ValidationFailure], instance: Option[A], _metadata: Map[String, List[Any]]) extends BindingAndValidationSummary[A](failures, _metadata) {

  private var _valuesMap: Map[String, Seq[String]] = _

  def valuesMap = _valuesMap

  protected[Failure] def valuesMap_=(value: Map[String, Seq[String]]): Unit = {
    _valuesMap = value
  }

}

object Failure {

  def apply[A](failures: List[ValidationFailure], valuesMap: Map[String, Seq[String]], instance: Option[A], metadata: Map[String, List[Any]]): Failure[A] = {
    val result = new Failure(failures, instance, metadata)
    result.valuesMap = valuesMap
    result
  }

}

