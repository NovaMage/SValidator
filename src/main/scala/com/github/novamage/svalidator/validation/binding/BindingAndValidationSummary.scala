package com.github.novamage.svalidator.validation.binding

import com.github.novamage.svalidator.validation.{ValidationFailure, ValidationSummary}

sealed abstract class BindingAndValidationSummary[+A](validationFailures: List[ValidationFailure]) extends ValidationSummary(validationFailures) {

  def instance: Option[A]

  def valuesMap: Map[String, Seq[String]]

}

object BindingAndValidationSummary {

  def empty[A]: BindingAndValidationSummary[A] = Failure(Nil, Map(), None.asInstanceOf[Option[A]])

  def filled[A](instance: A): BindingAndValidationSummary[A] = Success(instance, Map())

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

sealed case class Failure[+A] private(failures: List[ValidationFailure], instance: Option[A]) extends BindingAndValidationSummary[A](failures) {

  private var _valuesMap: Map[String, Seq[String]] = _

  def valuesMap = _valuesMap

  protected[Failure] def valuesMap_=(value: Map[String, Seq[String]]): Unit = {
    _valuesMap = value
  }

}

object Failure {

  def apply[A](failures: List[ValidationFailure], valuesMap: Map[String, Seq[String]], instance: Option[A]): Failure[A] = {
    val result = new Failure(failures, instance)
    result.valuesMap = valuesMap
    result
  }

}

