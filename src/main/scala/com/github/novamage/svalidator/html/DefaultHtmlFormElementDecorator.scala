package com.github.novamage.svalidator.html

object DefaultHtmlFormElementDecorator extends HtmlFormElementDecorator {

  override def decorateForm(formHtml: String, attributes: Map[String, Any]): String = ???

  override def decorateFormBody(formBodyHtml: String, attributes: Map[String, Any]): String = ???

  override def decorateTextBox(inputHtml: String, id: String, name: String, label: String, errors: List[String], attributes: Map[String, Any]): String = ???

  override def decoratePassword(inputHtml: String, id: String, name: String, label: String, errors: List[String], attributes: Map[String, Any]): String = ???

  override def decorateCheckBox(inputHtml: String, id: String, name: String, label: String, errors: List[String], attributes: Map[String, Any]): String = ???

  override def decorateSelect(selectHtml: String, id: String, name: String, errors: List[String], attributes: Map[String, Any]): String = ???

  override def decorateSelectOption(optionHtml: String, value: Any, text: Any, index: Int, attributes: Map[String, Any]): String = ???

  override def decorateRadioGroup(radioGroupHtml: String, name: String, errors: List[String], attributes: Map[String, Any]): String = ???

  override def decorateRadioGroupOption(radioHtml: String, id: String, name: String, label: String, attributes: Map[String, Any]): String = ???

  override def decorateCheckBoxGroup(radioGroupHtml: String, name: String, errors: List[String], attributes: Map[String, Any]): String = ???

  override def decorateCheckBoxGroupOption(checkBoxHtml: String, id: String, name: String, label: String, attributes: Map[String, Any]): String = ???

  override def decorateTextArea(textAreaHtml: String, id: String, name: String, errors: List[String], attributes: Map[String, Any]): String = ???

  override def decorateButton(buttonHtml: String, id: String, name: String, value: String, attributes: Map[String, Any]): String = ???

  override def decorateSubmit(submitHtml: String, id: String, name: String, value: String, attributes: Map[String, Any]): String = ???
}
