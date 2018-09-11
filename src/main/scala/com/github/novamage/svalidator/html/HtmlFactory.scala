package com.github.novamage.svalidator.html

import com.github.novamage.svalidator.validation.binding.BindingAndValidationWithData

/** Provides methods for generating html inputs alongside their errors for a
  * [[com.github.novamage.svalidator.validation.binding.BindingAndValidationWithData BindingAndValidationSummary]]
  *
  * @param converter Function to apply to generated markup strings on all methods of this class
  * @param inputDecorator Decorator for markup of generated html inputs
  * @param attributeDecorator Decorator for attributes of html inputs before they are generated
  * @param presenter Value converter for fields to be displayed in html inputs
  * @tparam A The type that all methods of this class return after being converted with the conversion function
  */
class HtmlFactory[A](converter: String => A,
                     inputDecorator: HtmlFormElementDecorator = DefaultHtmlFormElementDecorator,
                     attributeDecorator: HtmlAttributeDecorator = DefaultHtmlAttributeDecorator,
                     presenter: HtmlValuePresenter = DefaultHtmlValuePresenter) {

  /** Generates a &lt;form&gt; with the given action, method, enctype and extra attributes
    *
    * @param action The form's action
    * @param method Form method, usually GET or POST
    * @param enctype Form's enctype, usually application/x-www-form-urlencoded or multipart/form-data
    * @param attributes Any additional attributes to add to the form
    * @param body Content to put inside the form tag
    * @return Converted markup of the generated and decorated &lt;form&gt; element
    */
  def form(action: String,
           method: String,
           enctype: String,
           attributes: Map[String, Any])(body: => String): A = {
    val allAttributes = Map("action" -> action, "method" -> method, "enctype" -> enctype) ++ attributes
    decoratedHtmlFor(FormElementType.Form, allAttributes, inputDecorator.decorateFormBody(body, _), errors = Nil) { (html, decoratedAttrs) =>
      converter.apply(inputDecorator.decorateForm(html, decoratedAttrs))
    }
  }

  /** Generates a &lt;input type="hidden"&gt; with the given name
    *
    * @param summary Summary whose field will be extracted into a hidden
    * @param name Name attribute for the generated hidden input
    * @param valueGetter Value provider from the summary for the property of this hidden input
    * @param attributes Any additional attributes to put on the hidden input
    * @tparam B Type of the instance validated by the summary
    * @return The markup of the hidden input after applying the converter function
    */
  def hidden[B](summary: BindingAndValidationWithData[B, _],
                name: String,
                valueGetter: B => Any,
                attributes: Map[String, Any] = Map.empty): A = {
    val defaultId = getDefaultIdForName(name)
    val valueAttribute = getValueUsing(summary, valueGetter, name).map("value" -> _)
    val allAttributes = Map("type" -> "hidden", "id" -> defaultId, "name" -> name) ++ valueAttribute ++ attributes
    decoratedHtmlFor(FormElementType.Hidden, allAttributes, None, Nil) { (html, _) =>
      converter.apply(html)
    }
  }

  /** Generates a &lt;input type="text"&gt; with the given name and label
    *
    * @param summary Summary whose field will be extracted
    * @param name Name attribute for the generated input
    * @param valueGetter Value provider from the summary for the property of this input
    * @param label Label to place alongside this input
    * @param attributes Any additional attributes to put on the input
    * @tparam B Type of the instance validated by the summary
    * @return The markup of the input after applying the converter function
    */
  def textBox[B](summary: BindingAndValidationWithData[B, _],
                 name: String,
                 valueGetter: B => Any,
                 label: String,
                 attributes: Map[String, Any] = Map.empty): A = {

    val defaultId = getDefaultIdForName(name)
    val valueAttribute = getValueUsing(summary, valueGetter, name).map(x => ("value", x))
    val allAttributes = Map(("type", "text"), ("id", defaultId), ("name", name)) ++ valueAttribute ++ attributes
    val errors = summary.validationFailures.filter(_.fieldName == name).map(_.message)
    decoratedHtmlFor(FormElementType.TextBox, allAttributes, None, errors) { (html, decoratedAttrs) =>
      converter.apply(inputDecorator.decorateTextBox(
        inputHtml = html,
        id = decoratedAttrs.getOrElse("id", "").toString,
        name = decoratedAttrs.getOrElse("name", "").toString,
        label = label,
        errors = errors,
        attributes = decoratedAttrs))
    }
  }

  /** Generates a &lt;input type="password"&gt; with the given name and label
    *
    * @param summary Summary whose field will be extracted
    * @param name Name attribute for the generated input
    * @param label Label to place alongside this input
    * @param attributes Any additional attributes to put on the input
    * @tparam B Type of the instance validated by the summary
    * @return The markup of the input after applying the converter function
    */
  def password[B](summary: BindingAndValidationWithData[B, _],
                  name: String,
                  label: String,
                  attributes: Map[String, Any] = Map.empty): A = {

    val defaultId = getDefaultIdForName(name)
    val allAttributes: Map[String, Any] = Map("type" -> "password", "id" -> defaultId, "name" -> name) ++ attributes
    val errors = summary.validationFailures.filter(_.fieldName == name).map(_.message)
    decoratedHtmlFor(FormElementType.Password, allAttributes, None, errors) { (html, decoratedAttrs) =>
      converter.apply(inputDecorator.decoratePassword(
        inputHtml = html,
        id = decoratedAttrs.getOrElse("id", "").toString,
        name = decoratedAttrs.getOrElse("name", "").toString,
        label = label,
        errors = errors,
        attributes = decoratedAttrs))
    }
  }

  /** Generates a &lt;input type="checkbox"&gt; with the given name and label
    *
    * @param summary Summary whose field will be extracted
    * @param name Name attribute for the generated input
    * @param valueGetter Value provider from the summary for the property of this input
    * @param label Label to place alongside this input
    * @param attributes Any additional attributes to put on the input
    * @tparam B Type of the instance validated by the summary
    * @return The markup of the input after applying the converter function
    */
  def checkBox[B](summary: BindingAndValidationWithData[B, _],
                  name: String,
                  valueGetter: B => Boolean,
                  label: String,
                  attributes: Map[String, Any] = Map.empty): A = {
    val defaultId = getDefaultIdForName(name)
    val valueOnInstance = summary.instance.exists(valueGetter)
    val valueOnValuesMap = summary.valuesMap.get(name).exists(x => x.headOption.getOrElse("") == "true")
    val checkedAttribute = if (valueOnInstance || valueOnValuesMap) Some("checked" -> "checked") else None

    val allAttributes = Map("type" -> "checkbox", "id" -> defaultId, "name" -> name, "value" -> "true") ++ checkedAttribute ++ attributes
    val errors = summary.validationFailures.filter(_.fieldName == name).map(_.message)
    decoratedHtmlFor(FormElementType.CheckBox, allAttributes, None, errors) { (html, decoratedAttrs) =>
      converter.apply(inputDecorator.decorateCheckBox(
        inputHtml = html,
        id = decoratedAttrs.getOrElse("id", "").toString,
        name = decoratedAttrs.getOrElse("name", "").toString,
        label = label,
        errors = errors,
        attributes = decoratedAttrs))
    }
  }

  /** Generates a &lt;select&gt; with the given name and label
    *
    * @param summary Summary whose field will be extracted
    * @param name Name attribute for the generated input
    * @param valueGetter Value provider from the summary for the property of this input
    * @param optionValuesAndText List of options to display. First attribute will be used as the value, second attribute as the text
    * @param label Label to place alongside this input
    * @param attributes Any additional attributes to put on the input
    * @tparam B Type of the instance validated by the summary
    * @return The markup of the input after applying the converter function
    */
  def select[B](summary: BindingAndValidationWithData[B, _],
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
        decoratedHtmlFor(FormElementType.SelectOption, optionAttributes, Some(text.toString), Nil) { (html, decoratedAttrs) =>
          inputDecorator.decorateSelectOption(
            optionHtml = html,
            value = decoratedAttrs.getOrElse("value", ""),
            text = text,
            index = index,
            attributes = decoratedAttrs)
        }
    } mkString ""

    val errors = summary.validationFailures.filter(_.fieldName == name).map(_.message)
    decoratedHtmlFor(FormElementType.Select, allAttributes, Some(optionsHtml), errors) { (html, decoratedAttrs) =>
      converter.apply(inputDecorator.decorateSelect(
        selectHtml = html,
        id = decoratedAttrs.getOrElse("id", "").toString,
        name = decoratedAttrs.getOrElse("name", "").toString,
        label = label,
        errors = errors,
        attributes = allAttributes))
    }
  }

  /** Generates a group of &lt;input type="radio"&gt; that share the given name, and are tagged as a group with the label
    *
    * @param summary Summary whose field will be extracted
    * @param name Name attribute for the group of inputs
    * @param valueGetter Value provider from the summary for the property of this input
    * @param optionValuesAndText List of radios to display. First attribute will be used as the value, second attribute as the label
    * @param label Label to place alongside the group of inputs
    * @param attributes Any additional attributes to pass to the group of inputs
    * @tparam B Type of the instance validated by the summary
    * @return The markup of the group of inputs after applying the converter function
    */
  def radioGroup[B](summary: BindingAndValidationWithData[B, _],
                    name: String,
                    valueGetter: B => Any,
                    optionValuesAndText: List[(Any, Any)],
                    label: String,
                    attributes: Map[String, Any] = Map.empty): A = {
    val defaultId = getDefaultIdForName(name)
    val selectedValue = getValueUsing(summary, valueGetter, name)

    val radioButtonsHtml = optionValuesAndText.zipWithIndex.map {
      case ((value, text), index) =>
        val defaultIdForOption = defaultId + "_" + value.toString
        val checked = selectedValue.contains(value.toString)
        val checkedAttribute = if (checked) Some("checked" -> "checked") else None
        val attributes = Map("id" -> defaultIdForOption, "name" -> name, "type" -> "radio", "value" -> value) ++ checkedAttribute
        decoratedHtmlFor(FormElementType.RadioGroupOption, attributes, None, Nil) { (html, decoratedAttrs) =>
          inputDecorator.decorateRadioGroupOption(
            radioHtml = html,
            id = decoratedAttrs.getOrElse("id", "").toString,
            name = decoratedAttrs.getOrElse("name", "").toString,
            label = text.toString,
            index = index,
            attributes = decoratedAttrs)
        }
    }
    val errors = summary.validationFailures.filter(_.fieldName == name).map(_.message)
    converter.apply(inputDecorator.decorateRadioGroup(
      radioGroupHtml = radioButtonsHtml.mkString,
      name = name,
      label = label,
      errors = errors,
      attributes = attributes))
  }

  /** Generates a group of &lt;input type="checkbox"&gt; that share the given name, and are tagged as a group with the label
    *
    * @param summary Summary whose field will be extracted
    * @param name Name attribute for the group of inputs
    * @param valueGetter Values provider from the summary for the property of this input
    * @param optionValuesAndText List of checkboxes to display. First attribute will be used as the value, second attribute as the label
    * @param label Label to place alongside the group of inputs
    * @param attributes Any additional attributes to pass to the group of inputs
    * @tparam B Type of the instance validated by the summary
    * @return The markup of the group of inputs after applying the converter function
    */
  def checkBoxGroup[B](summary: BindingAndValidationWithData[B, _],
                       name: String,
                       valueGetter: B => List[Any],
                       optionValuesAndText: List[(Any, Any)],
                       label: String,
                       attributes: Map[String, Any] = Map.empty): A = {
    val defaultId = getDefaultIdForName(name)
    val checkedValues = getValuesListUsing(summary, valueGetter, name)
    val checkboxesHtml = optionValuesAndText.zipWithIndex.map {
      case ((value, text), index) =>
        val defaultIdForOption = defaultId + "_" + value.toString
        val checked = checkedValues.contains(value.toString)
        val checkedAttribute = if (checked) Some("checked" -> "checked") else None
        val attributes = Map("id" -> defaultIdForOption, "name" -> name, "type" -> "checkbox", "value" -> value) ++ checkedAttribute
        decoratedHtmlFor(FormElementType.CheckBoxGroupOption, attributes, None, Nil) { (html, decoratedAttrs) =>
          inputDecorator.decorateCheckBoxGroupOption(
            checkBoxHtml = html,
            id = decoratedAttrs.getOrElse("id", "").toString,
            name = decoratedAttrs.getOrElse("name", "").toString,
            label = text.toString,
            index = index,
            attributes = decoratedAttrs)
        }
    }
    val errors = summary.validationFailures.filter(_.fieldName == name).map(_.message)
    converter.apply(inputDecorator.decorateCheckBoxGroup(checkboxesHtml.mkString, name, label, errors, attributes))
  }

  /** Generates a &lt;textarea&gt; with the given name and label
    *
    * @param summary Summary whose field will be extracted
    * @param name Name attribute for the generated textarea
    * @param valueGetter Value provider from the summary for the property of this textarea
    * @param label Label to place alongside this textarea
    * @param attributes Any additional attributes to put on the textarea
    * @tparam B Type of the instance validated by the summary
    * @return The markup of the input after applying the converter function
    */
  def textArea[B](summary: BindingAndValidationWithData[B, _],
                  name: String,
                  valueGetter: B => Any,
                  label: String,
                  attributes: Map[String, Any] = Map.empty): A = {
    val defaultId = getDefaultIdForName(name)
    val allAttributes = Map("id" -> defaultId, "name" -> name) ++ attributes
    val text = getValueUsing(summary, valueGetter, name)
    val errors = summary.validationFailures.filter(_.fieldName == name).map(_.message)
    decoratedHtmlFor(FormElementType.TextArea, allAttributes, text, errors) { (html, decoratedAttrs) =>
      converter.apply(inputDecorator.decorateTextArea(
        html,
        id = decoratedAttrs.getOrElse("id", "").toString,
        name = decoratedAttrs.getOrElse("name", "").toString,
        label,
        errors,
        decoratedAttrs))
    }
  }

  /** Generates a &lt;button&gt; with the given name and displayed text
    *
    * @param summary Summary whose field will be extracted
    * @param name Name attribute for the generated button
    * @param text Text to display on the button
    * @param attributes Any additional attributes to put on the button
    * @tparam B Type of the instance validated by the summary
    * @return The markup of the input after applying the converter function
    */
  def button[B](summary: BindingAndValidationWithData[B, _],
                name: String,
                text: String,
                attributes: Map[String, Any] = Map.empty): A = {

    val defaultId = getDefaultIdForName(name)
    val allAttributes = Map("id" -> defaultId, "name" -> name, "type" -> "button") ++ attributes
    decoratedHtmlFor(FormElementType.Button, allAttributes, None, Nil) { (html, decoratedAttrs) =>
      converter.apply(inputDecorator.decorateButton(
        buttonHtml = html,
        id = decoratedAttrs.getOrElse("id", "").toString,
        name = decoratedAttrs.getOrElse("name", "").toString,
        text = text,
        attributes = decoratedAttrs))
    }
  }

  /** Generates a &lt;input type="submit"&gt; with the given name and value
    *
    * @param summary Summary whose field will be extracted
    * @param name Name attribute for the generated button
    * @param value Value attribute, which is the text displayed for the button
    * @param attributes Any additional attributes to put on the button
    * @tparam B Type of the instance validated by the summary
    * @return The markup of the input after applying the converter function
    */
  def submit[B](summary: BindingAndValidationWithData[B, _],
                name: String,
                value: String,
                attributes: Map[String, Any] = Map.empty): A = {

    val defaultId = getDefaultIdForName(name)
    val allAttributes = Map("id" -> defaultId, "name" -> name, "type" -> "submit", "value" -> value) ++ attributes
    decoratedHtmlFor(FormElementType.Submit, allAttributes, None, Nil) { (html, decoratedAttrs) =>
      converter.apply(inputDecorator.decorateSubmit(
        html,
        id = decoratedAttrs.getOrElse("id", "").toString,
        name = decoratedAttrs.getOrElse("name", "").toString,
        value,
        decoratedAttrs))
    }
  }

  private def getDefaultIdForName[B](name: String) = {
    name.replace(".", "_").replace("[", "_").replace("]", "_")
  }

  private def decoratedHtmlFor[B](elementType: FormElementType,
                                  attributes: Map[String, Any],
                                  content: Option[String],
                                  errors: List[String])(decorator: (String, Map[String, Any]) => B): B = {
    decoratedHtmlFor(elementType, attributes, _ => content.getOrElse(""), errors)(decorator)
  }

  private def decoratedHtmlFor[B](elementType: FormElementType,
                                  attributes: Map[String, Any],
                                  content: Map[String, Any] => String,
                                  errors: List[String])(decorator: (String, Map[String, Any]) => B): B = {

    val decoratedAttributes = attributeDecorator.decorateAttributes(elementType, attributes, errors)
    val attributeString = decoratedAttributes map {
      case (attrName, attrValue) => "%s=\"%s\"".format(attrName, attrValue)
    } mkString " "
    val finalContent = content(decoratedAttributes).trim
    val element = if (finalContent.isEmpty && elementType != FormElementType.TextArea) {
      s"<${ elementType.htmlElementName } $attributeString />"
    } else {
      s"<${ elementType.htmlElementName } $attributeString >${ finalContent }</${ elementType.htmlElementName }>"
    }
    decorator(element, decoratedAttributes)
  }

  private def getValueUsing[B](summary: BindingAndValidationWithData[B, _],
                               valueGetter: B => Any,
                               name: String): Option[String] = {
    summary.instance.map(valueGetter) match {
      case Some(value) => presenter.getValueToPresentFor(value)
      case None => summary.valuesMap.get(name) match {
        case None => None
        case Some(listOfValues) => listOfValues.headOption.map(_.trim)
      }
    }
  }

  private def getValuesListUsing[B, C](summary: BindingAndValidationWithData[B, _], valueGetter: B => List[C], name: String): List[String] = {
    summary.instance.map(valueGetter).map(_.flatMap(listValue => presenter.getValueToPresentFor(listValue))) match {
      case Some(list) => list
      case None => summary.valuesMap.get(name) match {
        case None => Nil
        case Some(listOfValues) => listOfValues.toList
      }
    }
  }
}
