package com.github.novamage.svalidator.html

trait HtmlFormElementDecorator {

  def decorateForm(formHtml: String,
                   attributes: Map[String, Any]): String

  def decorateFormBody(formBodyHtml: String,
                       attributes: Map[String, Any]): String

  def decorateTextBox(inputHtml: String,
                      id: String,
                      name: String,
                      label: String,
                      errors: List[String],
                      attributes: Map[String, Any]): String

  def decoratePassword(inputHtml: String,
                       id: String,
                       name: String,
                       label: String,
                       errors: List[String],
                       attributes: Map[String, Any]): String

  def decorateCheckBox(inputHtml: String,
                       id: String,
                       name: String,
                       label: String,
                       errors: List[String],
                       attributes: Map[String, Any]): String

  def decorateSelect(selectHtml: String,
                     id: String,
                     name: String,
                     label: String,
                     errors: List[String],
                     attributes: Map[String, Any]): String

  def decorateSelectOption(optionHtml: String,
                           value: Any,
                           text: Any,
                           index: Int,
                           attributes: Map[String, Any]): String

  def decorateRadioGroup(radioGroupHtml: String,
                         name: String,
                         label:String,
                         errors: List[String],
                         attributes: Map[String, Any]): String

  def decorateRadioGroupOption(radioHtml: String,
                               id: String,
                               name: String,
                               label: String,
                               attributes: Map[String, Any]): String


  def decorateCheckBoxGroup(radioGroupHtml: String,
                            name: String,
                            label:String,
                            errors: List[String],
                            attributes: Map[String, Any]): String

  def decorateCheckBoxGroupOption(checkBoxHtml: String,
                                  id: String,
                                  name: String,
                                  label: String,
                                  attributes: Map[String, Any]): String

  def decorateTextArea(textAreaHtml: String,
                       id: String,
                       name: String,
                       label:String,
                       errors: List[String],
                       attributes: Map[String, Any]): String


  def decorateButton(buttonHtml: String,
                     id: String,
                     name: String,
                     value: String,
                     attributes: Map[String, Any]): String


  def decorateSubmit(submitHtml: String,
                     id: String,
                     name: String,
                     value: String,
                     attributes: Map[String, Any]): String
}
