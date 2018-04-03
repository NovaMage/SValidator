package com.github.novamage.svalidator.html

import com.github.novamage.svalidator.validation.binding.BindingAndValidationSummary

class HTMLFactory[A](converter: String => A,
                     decorator: HtmlInputDecorator,
                     extractor: HtmlValueExtractor) {

  private def getValueUsing[B](summary: BindingAndValidationSummary[B],
                               valueGetter: (B) => Any,
                               name: String): Option[String] = {
    summary.instance.map(valueGetter) match {
      case Some(value) => extractor.extractValueFromProperty(value)
      case None => summary.valuesMap.get(name) match {
        case None => None
        case Some(listOfValues) => listOfValues.headOption.map(_.trim)
      }
    }
  }

  def textBox[B](summary: BindingAndValidationSummary[B],
                 name: String,
                 valueGetter: B => Any,
                 label: String,
                 attributes: Map[String, Any] = Map.empty): A = {

    val defaultId = getDefaultIdForName(name)
    val valueAttribute = getValueUsing(summary, valueGetter, name).map(x => ("value", x))
    val allAttributes: Map[String, Any] = Map(("type", "text"), ("id", defaultId), ("name", name)) ++ valueAttribute ++ attributes
    val element = htmlFor("input", allAttributes, None)
    val errors = summary.validationFailures.filter(_.fieldName == name).map(_.message)
    val decoratedElement = decorator.decorateTextBox(element, name, errors)
    converter(decoratedElement)
  }


  def checkBox[B](summary: BindingAndValidationSummary[B],
                  name: String,
                  valueGetter: B => Boolean,
                  label: String,
                  attributes: Map[String, Any] = Map()): A = {
    val defaultId = getDefaultIdForName(name)
    val valueOnInstance = summary.instance.exists(valueGetter)
    val valueOnValuesMap = summary.valuesMap.get(name).exists(x => x.headOption.getOrElse("") == "true")
    val checkedAttribute = if (valueOnInstance || valueOnValuesMap) Some("checked" -> "checked") else None

    val allAttributes = Map("type" -> "checkbox", "id" -> defaultId, "name" -> name, "value" -> "true") ++ checkedAttribute ++ attributes
    val element = htmlFor("input", allAttributes, None)
    val errors = summary.validationFailures.filter(_.fieldName == name).map(_.message)
    val decoratedElement = decorator.decorateCheckBox(element, name, errors)
    converter(decoratedElement)
  }

  def select[B](summary: BindingAndValidationSummary[B],
                name: String,
                valueGetter: B => Any,
                optionValuesAndText: List[(Any, Any)],
                label: String,
                attributes: Map[String, Any] = Map()): A = {
    val defaultId = getDefaultIdForName(name)
    val allAttributes = Map("id" -> defaultId, "name" -> name) ++ attributes
    val selectedValue = getValueUsing(summary, valueGetter, name).getOrElse("")
    val optionsHtml = optionValuesAndText.zipWithIndex.map {
      case ((value, text), index) =>
        val option = if (value.toString == selectedValue) {
          "<option value=\"" + value + "\" selected=\"selected\">" + text + "</>"
        } else {
          "<option value=\"" + value + "\">" + text + "</>"
        }
        decorator.decorateSelectOption(option, value, text, index)
    } mkString ""

    val element = htmlFor("select", allAttributes, Some(optionsHtml))
    val errors = summary.validationFailures.filter(_.fieldName == name).map(_.message)
    decorator.decorateSelect(element, name, errors)
    converter(element)
  }


  def radioGroup[B](summary: BindingAndValidationSummary[B],
                    name: String,
                    valueGetter: B => Any,
                    optionValuesAndText: List[(Any, Any)],
                    label: String) = {
    val defaultId = getDefaultIdForName(name)
    val selectedValue = getValueUsing(summary, valueGetter, name).getOrElse("")
    val radioButtonsHtml = optionValuesAndText.zipWithIndex.map {
      case ((value, text), index) =>
        val idForInput = defaultId + (if (index == 0) "" else index.toString)
        val checked = selectedValue == value.toString
        val checkedAttribute = if (checked) Some("checked" -> "checked") else None
        val attributes = Map("id" -> idForInput, name -> name, "type" -> "radio", "value" -> value) ++ checkedAttribute
        htmlFor("input", attributes, None)
    }
    converter(radioButtonsHtml.mkString)
  }

  private def getDefaultIdForName[B](name: String) = {
    name.replace(".", "_").replace("[", "_").replace("]", "_")
  }

  private def htmlFor(elementName: String,
                             attributes: Map[String, Any],
                             content: Option[String]): String = {

    val attributeString = attributes map {
      case (attrName, attrValue) => "%s=\"%s\"".format(attrName, attrValue)
    } mkString " "
    content match {
      case Some(c) => s"<$elementName $attributeString >$c</$elementName>"
      case None => s"<$elementName $attributeString />"
    }
  }
}
