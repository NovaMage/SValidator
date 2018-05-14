package com.github.novamage.svalidator.validation.binding

import com.github.novamage.svalidator.validation.{ValidationFailure, ValidationSummary}

import scala.language.implicitConversions

sealed abstract class BindingAndValidationSummary[+A](validationFailures: List[ValidationFailure]) extends ValidationSummary(validationFailures) {

  def instance: Option[A]

  def valuesMap: Map[String, Seq[String]]

  def map[B](f: A => B): BindingAndValidationSummary[B] = {
    this match {
      case Success(value) => Success(f(value), valuesMap)
      case Failure(failures) => Failure(failures, valuesMap, instance.map(f))
    }
  }

}

object BindingAndValidationSummary {

  def empty[A]: BindingAndValidationSummary[A] = Failure(Nil, Map.empty, None)

  def filled[A](instance: A): BindingAndValidationSummary[A] = Success(instance, Map.empty)

}

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

object Success {

  def apply[A](instanceValue: A, valuesMap: Map[String, Seq[String]]): Success[A] = {
    val result = new Success[A](instanceValue)
    result.valuesMap = valuesMap
    result
  }

  def unapply[A](input: Success[A]): Option[A] = input.instance

}

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

object Failure {


  def apply[A](failures: List[ValidationFailure], valuesMap: Map[String, Seq[String]], instance: Option[A]): Failure[A] = {
    val result = new Failure(failures, instance)
    result.valuesMap = valuesMap
    result
  }

  def unapply[A](input: Failure[A]): Option[List[ValidationFailure]] = {
    Some(input.validationFailures)
  }

}

