package com.github.novamage.svalidator.validation.binding

import com.github.novamage.svalidator.validation.{ValidationFailure, ValidationSummary}

class BindingAndValidationSummary[A](private val failures: List[ValidationFailure], val instance: Option[A], val valuesMap: Map[String, Seq[String]]) extends ValidationSummary(failures) {

  override def equals(obj: Any) = {

    val other = obj.asInstanceOf[BindingAndValidationSummary[A]]
    other != null && other.failures == this.failures && other.instance == this.instance && other.valuesMap == this.valuesMap
  }
}

object BindingAndValidationSummary {

  def apply[A](failures: List[ValidationFailure], instance: Option[A], valuesMap: Map[String, Seq[String]]) = new BindingAndValidationSummary[A](failures, instance, valuesMap)

  def empty[A] = new BindingAndValidationSummary[A](Nil, None, Map())

}
