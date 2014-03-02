package com.github.novamage.svalidator.validation.binding

import com.github.novamage.svalidator.validation.{ValidationFailure, ValidationSummary}

sealed abstract class BindingAndValidationSummary[+A](validationFailures: List[ValidationFailure]) extends ValidationSummary(validationFailures) {

  def instance: Option[A]

  def valuesMap: Map[String, Seq[String]]

}

object BindingAndValidationSummary {

  def empty[A] = Failure(Nil, Map())

  def filled[A](instance: A) = Success(instance, Map())

}

sealed case class Success[+A] private(instanceValue: A) extends BindingAndValidationSummary[A](Nil) {

  private var _valuesMap: Map[String, Seq[String]] = _

  def valuesMap = _valuesMap

  protected[Success] def valuesMap_=(value: Map[String, Seq[String]]): Unit = {
    _valuesMap = value
  }

  def instance: Option[A] = Some(instanceValue)
}

object Success {

  def apply[A](instanceValue: A, valuesMap: Map[String, Seq[String]]): Success[A] = {
    val result = new Success[A](instanceValue)
    result.valuesMap = valuesMap
    result
  }
}

sealed case class Failure private(failures: List[ValidationFailure]) extends BindingAndValidationSummary[Nothing](failures) {

  private var _valuesMap: Map[String, Seq[String]] = _

  def valuesMap = _valuesMap

  protected[Failure] def valuesMap_=(value: Map[String, Seq[String]]): Unit = {
    _valuesMap = value
  }

  def instance: Option[Nothing] = None
}

object Failure {

  def apply[A](failures: List[ValidationFailure], valuesMap: Map[String, Seq[String]]): Failure = {
    val result = new Failure(failures)
    result.valuesMap = valuesMap
    result
  }

}

