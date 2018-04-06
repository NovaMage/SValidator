package com.github.novamage.svalidator.html

import com.github.novamage.svalidator.validation.binding.BindingAndValidationSummary

class HTMLFactory[A](converter: String => A,
                     inputDecorator: HtmlFormElementDecorator,
                     attributeDecorator: HtmlAttributeDecorator,
                     extractor: HtmlValueExtractor) {

  def form(action: String,
           method: String = "POST",
           enctype: String = "application/x-www-form-urlencoded",
           attributes: Map[String, Any] = Map.empty)(body: => String): A = {
    val allAttributes = Map("action" -> action, "method" -> method, "enctype" -> enctype) ++ attributes
    val decoratedFormBody = inputDecorator.decorateFormBody(body, allAttributes)
    decoratedHtmlFor(FormElementType.Form, allAttributes, Some(decoratedFormBody)) { (html, decoratedAttrs) =>
      converter(inputDecorator.decorateForm(html, decoratedAttrs))
    }
  }

  def hidden[B](summary: BindingAndValidationSummary[B],
                name: String,
                valueGetter: B => Any,
                attributes: Map[String, Any] = Map.empty): A = {
    val defaultId = getDefaultIdForName(name)
    val valueAttribute = getValueUsing(summary, valueGetter, name).map("value" -> _)
    val allAttributes = Map("type" -> "hidden", "id" -> defaultId, "name" -> name) ++ valueAttribute ++ attributes
    decoratedHtmlFor(FormElementType.Hidden, allAttributes, None) { (html, _) =>
      converter(html)
    }
  }

  def textBox[B](summary: BindingAndValidationSummary[B],
                 name: String,
                 valueGetter: B => Any,
                 label: String,
                 attributes: Map[String, Any] = Map.empty): A = {

    val defaultId = getDefaultIdForName(name)
    val valueAttribute = getValueUsing(summary, valueGetter, name).map(x => ("value", x))
    val allAttributes = Map(("type", "text"), ("id", defaultId), ("name", name)) ++ valueAttribute ++ attributes
    decoratedHtmlFor(FormElementType.TextBox, allAttributes, None) { (html, decoratedAttrs) =>
      val errors = summary.validationFailures.filter(_.fieldName == name).map(_.message)
      val inputId = decoratedAttrs.apply("id").toString
      converter(inputDecorator.decorateTextBox(html, inputId, name, label, errors, decoratedAttrs))
    }
  }

  def password[B](summary: BindingAndValidationSummary[B],
                  name: String,
                  valueGetter: B => Any,
                  label: String,
                  attributes: Map[String, Any] = Map.empty): A = {

    val defaultId = getDefaultIdForName(name)
    val allAttributes: Map[String, Any] = Map("type" -> "password", "id" -> defaultId, "name" -> name) ++ attributes
    decoratedHtmlFor(FormElementType.Password, allAttributes, None) { (html, decoratedAttrs) =>
      val errors = summary.validationFailures.filter(_.fieldName == name).map(_.message)
      val inputId = decoratedAttrs.apply("id").toString
      converter(inputDecorator.decoratePassword(html, inputId, name, label, errors, decoratedAttrs))
    }
  }

  def checkBox[B](summary: BindingAndValidationSummary[B],
                  name: String,
                  valueGetter: B => Boolean,
                  label: String,
                  attributes: Map[String, Any] = Map.empty): A = {
    val defaultId = getDefaultIdForName(name)
    val valueOnInstance = summary.instance.exists(valueGetter)
    val valueOnValuesMap = summary.valuesMap.get(name).exists(x => x.headOption.getOrElse("") == "true")
    val checkedAttribute = if (valueOnInstance || valueOnValuesMap) Some("checked" -> "checked") else None

    val allAttributes = Map("type" -> "checkbox", "id" -> defaultId, "name" -> name, "value" -> "true") ++ checkedAttribute ++ attributes
    decoratedHtmlFor(FormElementType.CheckBox, allAttributes, None) { (html, decoratedAttrs) =>
      val errors = summary.validationFailures.filter(_.fieldName == name).map(_.message)
      val inputId = decoratedAttrs.apply("id").toString
      converter(inputDecorator.decorateCheckBox(html, inputId, name, label, errors, decoratedAttrs))
    }
  }

  def select[B](summary: BindingAndValidationSummary[B],
                name: String,
                valueGetter: B => Any,
                optionValuesAndText: List[(Any, Any)],
                label: String,
                attributes: Map[String, Any] = Map.empty): A = {
    val defaultId = getDefaultIdForName(name)
    val allAttributes = Map("id" -> defaultId, "name" -> name) ++ attributes
    val selectedValue = getValueUsing(summary, valueGetter, name).getOrElse("")
    val optionsHtml = optionValuesAndText.zipWithIndex.map {
      case ((value, text), index) =>
        val selectedAttribute = if (value.toString == selectedValue) Some("selected" -> "selected") else None
        val optionAttributes = Map("value" -> value.toString) ++ selectedAttribute
        decoratedHtmlFor(FormElementType.SelectOption, optionAttributes, Some(text.toString)) { (html, decoratedAttrs) =>
          inputDecorator.decorateSelectOption(html, value, text, index, decoratedAttrs)
        }
    } mkString ""

    decoratedHtmlFor(FormElementType.Select, allAttributes, Some(optionsHtml)) { (html, decoratedAttrs) =>
      val errors = summary.validationFailures.filter(_.fieldName == name).map(_.message)
      val inputId = decoratedAttrs.apply("id").toString
      converter(inputDecorator.decorateSelect(html, inputId, name, errors, allAttributes))
    }
  }


  def radioGroup[B](summary: BindingAndValidationSummary[B],
                    name: String,
                    valueGetter: B => Any,
                    optionValuesAndText: List[(Any, Any)],
                    label: String,
                    attributes: Map[String, Any] = Map.empty): A = {
    val defaultId = getDefaultIdForName(name)
    val selectedValue = getValueUsing(summary, valueGetter, name)
    val radioButtonsHtml = optionValuesAndText.zipWithIndex.map {
      case (value, text) =>
        val defaultIdForOption = defaultId + "_" + value.toString
        val checked = selectedValue.contains(value.toString)
        val checkedAttribute = if (checked) Some("checked" -> "checked") else None
        val attributes = Map("id" -> defaultIdForOption, name -> name, "type" -> "radio", "value" -> value) ++ checkedAttribute
        decoratedHtmlFor(FormElementType.RadioGroupOption, attributes, None) { (html, decoratedAttrs) =>
          val inputId = decoratedAttrs.getOrElse("id", "").toString
          inputDecorator.decorateRadioGroupOption(html, inputId, name, text.toString, decoratedAttrs)
        }
    }
    val errors = summary.validationFailures.filter(_.fieldName == name).map(_.message)
    converter(inputDecorator.decorateRadioGroup(radioButtonsHtml.mkString, name, errors, attributes))
  }

  def checkBoxGroup[B](summary: BindingAndValidationSummary[B],
                       name: String,
                       valueGetter: B => List[Any],
                       optionValuesAndText: List[(Any, Any)],
                       label: String,
                       attributes: Map[String, Any]): A = {
    val defaultId = getDefaultIdForName(name)
    val checkedValues = getValuesListUsing(summary, valueGetter, name)
    val checkboxesHtml = optionValuesAndText.map {
      case (value, text) =>
        val defaultIdForOption = defaultId + "_" + value.toString
        val checked = checkedValues.contains(value.toString)
        val checkedAttribute = if (checked) Some("checked" -> "checked") else None
        val attributes = Map("id" -> defaultIdForOption, name -> name, "type" -> "checkbox", "value" -> value) ++ checkedAttribute
        decoratedHtmlFor(FormElementType.CheckBoxGroupOption, attributes, None) { (html, decoratedAttrs) =>
          val inputId = decoratedAttrs.getOrElse("id", "").toString
          inputDecorator.decorateCheckBoxGroupOption(html, inputId, name, text.toString, decoratedAttrs)
        }
    }
    val errors = summary.validationFailures.filter(_.fieldName == name).map(_.message)
    converter(inputDecorator.decorateCheckBoxGroup(checkboxesHtml.mkString, name, errors, attributes))
  }

  def textArea[B](summary: BindingAndValidationSummary[B],
                  name: String,
                  valueGetter: B => Any,
                  label: String,
                  attributes: Map[String, Any] = Map.empty): A = {
    val defaultId = getDefaultIdForName(name)
    val allAttributes = Map("id" -> defaultId, "name" -> name) ++ attributes
    val text = getValueUsing(summary, valueGetter, name)
    decoratedHtmlFor(FormElementType.TextArea, allAttributes, text) { (html, decoratedAttrs) =>
      val inputId = decoratedAttrs.getOrElse("id", "").toString
      val errors = summary.validationFailures.filter(_.fieldName == name).map(_.message)
      converter(inputDecorator.decorateTextArea(html, inputId, name, errors, decoratedAttrs))
    }
  }

  def button[B](summary: BindingAndValidationSummary[B],
                name: String,
                value: String,
                attributes: Map[String, Any] = Map.empty): A = {

    val defaultId = getDefaultIdForName(name)
    val allAttributes = Map("id" -> defaultId, "name" -> name, "type" -> "button") ++ attributes
    decoratedHtmlFor(FormElementType.Button, allAttributes, None) { (html, decoratedAttrs) =>
      val inputId = decoratedAttrs.getOrElse("id", "").toString
      converter(inputDecorator.decorateButton(html, inputId, name, value, decoratedAttrs))
    }
  }

  def submit[B](summary: BindingAndValidationSummary[B],
                name: String,
                value: String,
                attributes: Map[String, Any] = Map.empty): A = {

    val defaultId = getDefaultIdForName(name)
    val allAttributes = Map("id" -> defaultId, "name" -> name, "type" -> "submit", "value" -> value) ++ attributes
    decoratedHtmlFor(FormElementType.Submit, allAttributes, None) { (html, decoratedAttrs) =>
      val inputId = decoratedAttrs.getOrElse("id", "").toString
      converter(inputDecorator.decorateSubmit(html, inputId, name, value, decoratedAttrs))
    }
  }

  private def getDefaultIdForName[B](name: String) = {
    name.replace(".", "_").replace("[", "_").replace("]", "_")
  }

  private def decoratedHtmlFor[B](elementType: FormElementType,
                                  attributes: Map[String, Any],
                                  content: Option[String])(decorator: (String, Map[String, Any]) => B): B = {

    val decoratedAttributes = attributeDecorator.decorateAttributes(elementType, attributes)
    val attributeString = decoratedAttributes map {
      case (attrName, attrValue) => "%s=\"%s\"".format(attrName, attrValue)
    } mkString " "
    val element = content match {
      case Some(c) => s"<${ elementType.htmlElementName } $attributeString >$c</${ elementType.htmlElementName }>"
      case None => s"<${ elementType.htmlElementName } $attributeString />"
    }
    decorator(element, decoratedAttributes)
  }

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

  private def getValuesListUsing[B, C](summary: BindingAndValidationSummary[B], valueGetter: (B) => List[C], name: String): List[String] = {
    summary.instance.map(valueGetter).map(_.flatMap(listValue => extractor.extractValueFromProperty(listValue))) match {
      case Some(list) => list
      case None => summary.valuesMap.get(name) match {
        case None => Nil
        case Some(listOfValues) => listOfValues.toList
      }
    }
  }
}
