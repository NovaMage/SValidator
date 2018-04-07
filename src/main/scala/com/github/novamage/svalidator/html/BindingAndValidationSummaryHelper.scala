package com.github.novamage.svalidator.html

import com.github.novamage.svalidator.validation.binding.BindingAndValidationSummary

class BindingAndValidationSummaryHelper[A](summary: BindingAndValidationSummary[A]) {

  def hidden[B](name: String,
                valueGetter: A => Any,
                attributes: Map[String, Any] = Map.empty)(implicit htmlFactory: HtmlFactory[B]): B = {
    htmlFactory.hidden(summary, name, valueGetter, attributes)
  }

  def textBox[B](name: String,
                 valueGetter: A => Any,
                 label: String,
                 attributes: Map[String, Any] = Map.empty)(implicit htmlFactory: HtmlFactory[B]): B = {
    htmlFactory.textBox(summary, name, valueGetter, label, attributes)
  }

  def password[B](name: String,
                  label: String,
                  attributes: Map[String, Any] = Map.empty)(implicit htmlFactory: HtmlFactory[B]): B = {
    htmlFactory.password(summary, name, label, attributes)
  }

  def checkBox[B](name: String,
                  valueGetter: A => Boolean,
                  label: String,
                  attributes: Map[String, Any] = Map.empty)(implicit htmlFactory: HtmlFactory[B]): B = {
    htmlFactory.checkBox(summary, name, valueGetter, label, attributes)
  }

  def select[B](name: String,
                valueGetter: A => Any,
                optionValuesAndText: List[(Any, Any)],
                label: String,
                attributes: Map[String, Any] = Map.empty)(implicit htmlFactory: HtmlFactory[B]): B = {
    htmlFactory.select(summary, name, valueGetter, optionValuesAndText, label, attributes)
  }

  def radioGroup[B](name: String,
                    valueGetter: A => Any,
                    optionValuesAndText: List[(Any, Any)],
                    label: String,
                    attributes: Map[String, Any] = Map.empty)(implicit htmlFactory: HtmlFactory[B]): B = {
    htmlFactory.radioGroup(summary, name, valueGetter, optionValuesAndText, label, attributes)
  }

  def checkBoxGroup[B](name: String,
                       valueGetter: A => List[Any],
                       optionValuesAndText: List[(Any, Any)],
                       label: String,
                       attributes: Map[String, Any] = Map.empty)(implicit htmlFactory: HtmlFactory[B]): B = {
    htmlFactory.checkBoxGroup(summary, name, valueGetter, optionValuesAndText, label, attributes)
  }

  def textArea[B](name: String,
                  valueGetter: A => Any,
                  label: String,
                  attributes: Map[String, Any] = Map.empty)(implicit htmlFactory: HtmlFactory[B]): B = {
    htmlFactory.textArea(summary, name, valueGetter, label, attributes)
  }


  def button[B](name: String,
                value: String,
                attributes: Map[String, Any] = Map.empty)(implicit htmlFactory: HtmlFactory[B]): B = {
    htmlFactory.button(summary, name, value, attributes)
  }

  def submit[B](name: String,
                value: String,
                attributes: Map[String, Any] = Map.empty)(implicit htmlFactory: HtmlFactory[B]): B = {
    htmlFactory.submit(summary, name, value, attributes)
  }

}

object BindingAndValidationSummaryHelper {

  implicit def helper[A](summary: BindingAndValidationSummary[A]): BindingAndValidationSummaryHelper[A] = {
    new BindingAndValidationSummaryHelper(summary)
  }

}

