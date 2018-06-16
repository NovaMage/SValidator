package com.github.novamage.svalidator.html

/** Provides methods for intercepting the creation process of inputs in the
  * [[com.github.novamage.svalidator.html.HtmlFactory HtmlFactory]] for the purposes of styling by surrounding the
  * target element with additional markup.  This is the final stage of the decoration pipeline and happens after attribute
  * decoration
  */
trait HtmlFormElementDecorator {

  /** Intercepts the creation of &lt;form&gt; elements.
    *
    * @param formHtml   Generated &lt;form&gt; markup so far, including its body
    * @param attributes Element attributes after being decorated by the attribute decorator
    * @return Decorated final markup to be used for the form
    */
  def decorateForm(formHtml: String,
                   attributes: Map[String, Any]): String

  /** Intercepts the creation of the internal content of &lt;form&gt; elements.
    *
    * @param formBodyHtml The content that will be placed inside a form's body
    * @param attributes   Element attributes for the form, after being decorated by the attribute decorator
    * @return Decorated final markup of the form body to be placed inside the form
    */
  def decorateFormBody(formBodyHtml: String,
                       attributes: Map[String, Any]): String

  /** Intercepts the creation of &lt;input type="text"&gt; elements.
    *
    * @param inputHtml  Generated &lt;input&gt; markup so far
    * @param id         Id provided for the input, after decoration
    * @param name       Name provided for the input, after decoration
    * @param label      Label provided for the input
    * @param errors     Errors associated to this field in the validation summary being processed
    * @param attributes Element attributes for the input, after being decorated by the attribute decorator
    * @return Decorated final markup of the input
    */
  def decorateTextBox(inputHtml: String,
                      id: String,
                      name: String,
                      label: String,
                      errors: List[String],
                      attributes: Map[String, Any]): String

  /** Intercepts the creation of &lt;input type="password"&gt; elements.
    *
    * @param inputHtml  Generated &lt;input&gt; markup so far
    * @param id         Id provided for the input, after decoration
    * @param name       Name provided for the input, after decoration
    * @param label      Label provided for the input
    * @param errors     Errors associated to this field in the validation summary being processed
    * @param attributes Element attributes for the input, after being decorated by the attribute decorator
    * @return Decorated final markup of the input
    */
  def decoratePassword(inputHtml: String,
                       id: String,
                       name: String,
                       label: String,
                       errors: List[String],
                       attributes: Map[String, Any]): String

  /** Intercepts the creation of a single labeled &lt;input type="checkbox"&gt; element.
    *
    * @param inputHtml  Generated &lt;input&gt; markup so far
    * @param id         Id provided for the input, after decoration
    * @param name       Name provided for the input, after decoration
    * @param label      Label provided for the input
    * @param errors     Errors associated to this field in the validation summary being processed
    * @param attributes Element attributes for the input, after being decorated by the attribute decorator
    * @return Decorated final markup of the input
    */
  def decorateCheckBox(inputHtml: String,
                       id: String,
                       name: String,
                       label: String,
                       errors: List[String],
                       attributes: Map[String, Any]): String

  /** Intercepts the creation of &lt;select&gt; elements.
    *
    * @param selectHtml Generated &lt;select&gt; markup so far, including its options
    * @param id         Id provided for the input, after decoration
    * @param name       Name provided for the input, after decoration
    * @param label      Label provided for the input
    * @param errors     Errors associated to this field in the validation summary being processed
    * @param attributes Element attributes for the select, after being decorated by the attribute decorator
    * @return Decorated final markup of the select
    */
  def decorateSelect(selectHtml: String,
                     id: String,
                     name: String,
                     label: String,
                     errors: List[String],
                     attributes: Map[String, Any]): String

  /** Intercepts the creation of &lt;option&gt; elements for &lt;select&gt; elements.
    *
    * @param optionHtml Generated &lt;option&gt; markup so far
    * @param value      Value for the option, after decoration
    * @param text       Text for the option
    * @param index      Zero-based index of the current option within the select's options
    * @param attributes Element attributes for the option, after being decorated by the attribute decorator
    * @return Decorated final markup of the option
    */
  def decorateSelectOption(optionHtml: String,
                           value: Any,
                           text: Any,
                           index: Int,
                           attributes: Map[String, Any]): String

  /** Intercepts the creation of a group of &lt;input type="radio"&gt; elements that will share the same name.
    *
    * @param radioGroupHtml Generated markup so far for the group of &lt;input type="radio"&gt; options
    * @param name           Name provided for the inputs
    * @param label          Label provided for the inputs
    * @param errors         Errors associated to this field in the validation summary being processed
    * @param attributes     Element attributes for the group of radios
    * @return Decorated final markup of the radio group
    */
  def decorateRadioGroup(radioGroupHtml: String,
                         name: String,
                         label: String,
                         errors: List[String],
                         attributes: Map[String, Any]): String

  /** Intercepts the creation of a single &lt;input type="radio"&gt; within a group of them that will share
    * the same name.
    *
    * @param radioHtml  Generated markup so far for the &lt;input type="radio"&gt; option
    * @param id         Id provided for the input, after decoration
    * @param name       Name provided for the input, after decoration
    * @param label      Label provided for the input
    * @param index      Zero-based index of this radio within the group of radios
    * @param attributes Element attributes for this specific radio, after being decorated by the attribute decorator
    * @return Decorated final markup of this specific radio
    */
  def decorateRadioGroupOption(radioHtml: String,
                               id: String,
                               name: String,
                               label: String,
                               index: Int,
                               attributes: Map[String, Any]): String


  /** Intercepts the creation of a group of &lt;input type="checkbox"&gt; elements that will share the same name.
    *
    * @param checkBoxGroupHtml Generated markup so far for the group of &lt;input type="checkbox"&gt; options
    * @param name              Name provided for the inputs, after decoration
    * @param label             Label provided for the inputs
    * @param errors            Errors associated to this field in the validation summary being processed
    * @param attributes        Element attributes for the group of checkboxes
    * @return Decorated final markup of the checkbox group
    */
  def decorateCheckBoxGroup(checkBoxGroupHtml: String,
                            name: String,
                            label: String,
                            errors: List[String],
                            attributes: Map[String, Any]): String

  /** Intercepts the creation of a single &lt;input type="checkbox"&gt; within a group of them that will share
    * the same name.
    *
    * @param checkBoxHtml Generated markup so far for the &lt;input type="radio"&gt; option
    * @param id           Id provided for the input, after decoration
    * @param name         Name provided for the input, after decoration
    * @param label        Label provided for the input
    * @param index        Zero-based index of this checkbox within the group of checkboxes
    * @param attributes   Element attributes for this specific checkbox, after being decorated by the attribute decorator
    * @return Decorated final markup of this specific  checkbox
    */
  def decorateCheckBoxGroupOption(checkBoxHtml: String,
                                  id: String,
                                  name: String,
                                  label: String,
                                  index: Int,
                                  attributes: Map[String, Any]): String

  /** Intercepts the creation of &lt;textarea&gt; elements.
    *
    * @param textAreaHtml Generated &lt;textarea&gt; markup so far
    * @param id           Id provided for the textarea, after decoration
    * @param name         Name provided for the textarea, after decoration
    * @param label        Label provided for the textarea
    * @param errors       Errors associated to this field in the validation summary being processed
    * @param attributes   Element attributes for the textarea, after being decorated by the attribute decorator
    * @return Decorated final markup of the textarea
    */
  def decorateTextArea(textAreaHtml: String,
                       id: String,
                       name: String,
                       label: String,
                       errors: List[String],
                       attributes: Map[String, Any]): String


  /** Intercepts the creation of &lt;button&gt; elements
    *
    * @param buttonHtml The button markup generated so far
    * @param id         Id provided for the textarea, after decoration
    * @param name       Name provided for the textarea, after decoration
    * @param text       Text to be shown for the button
    * @param attributes Button attributes, after decoration
    * @return Decorated final markup of the button
    */
  def decorateButton(buttonHtml: String,
                     id: String,
                     name: String,
                     text: String,
                     attributes: Map[String, Any]): String

  /** Intercepts the creation of &lt;button&gt; elements
    *
    * @param submitHtml The submit markup generated so far
    * @param id         Id provided for the textarea, after decoration
    * @param name       Name provided for the textarea, after decoration
    * @param value      Value attribute for the submit, after decoration
    * @param attributes Submit attributes, after decoration
    * @return Decorated final markup of the submit
    */
  def decorateSubmit(submitHtml: String,
                     id: String,
                     name: String,
                     value: String,
                     attributes: Map[String, Any]): String
}
