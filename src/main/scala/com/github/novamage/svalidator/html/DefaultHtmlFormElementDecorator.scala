package com.github.novamage.svalidator.html

/** Default implementation for [[com.github.novamage.svalidator.html.HtmlFormElementDecorator HtmlFormElementDecorator]]
  */
object DefaultHtmlFormElementDecorator extends HtmlFormElementDecorator {

  private def errorClassFor(errors: List[String]): String = if (errors.nonEmpty) " class=\"has-errors\"" else ""

  override def decorateForm(formHtml: String,
                            attributes: Map[String, Any]): String = formHtml

  override def decorateFormBody(formBodyHtml: String,
                                attributes: Map[String, Any]): String = formBodyHtml

  override def decorateTextBox(inputHtml: String,
                               id: String,
                               name: String,
                               label: String,
                               errors: List[String],
                               attributes: Map[String, Any]): String = decorateStandardInput(inputHtml, id, label, errors)


  override def decoratePassword(inputHtml: String,
                                id: String,
                                name: String,
                                label: String,
                                errors: List[String],
                                attributes: Map[String, Any]): String = decorateStandardInput(inputHtml, id, label, errors)

  override def decorateCheckBox(inputHtml: String,
                                id: String,
                                name: String,
                                label: String,
                                errors: List[String],
                                attributes: Map[String, Any]): String = decorateStandardInput(inputHtml, id, label, errors)

  override def decorateSelect(selectHtml: String,
                              id: String,
                              name: String,
                              label: String,
                              errors: List[String],
                              attributes: Map[String, Any]): String = decorateStandardInput(selectHtml, id, label, errors)

  override def decorateSelectOption(optionHtml: String,
                                    value: Any,
                                    text: Any,
                                    index: Int,
                                    attributes: Map[String, Any]): String = optionHtml

  override def decorateRadioGroup(radioGroupHtml: String,
                                  name: String,
                                  label: String,
                                  errors: List[String],
                                  attributes: Map[String, Any]): String = {
    s"""
       |<div${errorClassFor(errors)}>
       |<label>$label</label>
       |<div>
       |$radioGroupHtml
       |</div>
       |${ appendErrors("", errors) }
       |</div>
    """.stripMargin
  }

  override def decorateRadioGroupOption(radioHtml: String,
                                        id: String,
                                        name: String,
                                        label: String,
                                        index: Int,
                                        attributes: Map[String, Any]): String = {
    s"""
       |<label for="$id">$label</label>
       |$radioHtml
    """.stripMargin

  }

  override def decorateCheckBoxGroup(checkBoxGroupHtml: String,
                                     name: String,
                                     label: String,
                                     errors: List[String],
                                     attributes: Map[String, Any]): String = {
    s"""
       |<div${errorClassFor(errors)}>
       |<label>$label</label>
       |<div>
       |$checkBoxGroupHtml
       |</div>
       |${ appendErrors("", errors) }
       |</div>
    """.stripMargin
  }

  override def decorateCheckBoxGroupOption(checkBoxHtml: String,
                                           id: String,
                                           name: String,
                                           label: String,
                                           index: Int,
                                           attributes: Map[String, Any]): String = {

    s"""
       |<label for="$id">$label</label>
       |$checkBoxHtml
    """.stripMargin
  }

  override def decorateTextArea(textAreaHtml: String,
                                id: String,
                                name: String,
                                label: String,
                                errors: List[String],
                                attributes: Map[String, Any]): String = decorateStandardInput(textAreaHtml, id, label, errors)

  override def decorateButton(buttonHtml: String,
                              id: String,
                              name: String,
                              value: String,
                              attributes: Map[String, Any]): String = {
    s"""
       |<div>
       |$buttonHtml
       |</div>
    """.stripMargin
  }

  override def decorateSubmit(submitHtml: String,
                              id: String,
                              name: String,
                              value: String,
                              attributes: Map[String, Any]): String = {
    s"""
       |<div>
       |$submitHtml
       |</div>
    """.stripMargin
  }

  private def appendErrors(elementHtml: String, errors: List[String]): String = {
    val errorsString = errors.map(error => s"""<span class="error">$error</span>""").mkString
    s"$elementHtml$errorsString"
  }

  private def decorateStandardInput(inputHtml: String,
                                    id: String,
                                    label: String,
                                    errors: List[String]): String = {

    s"""
       |<div${errorClassFor(errors)}>
       |<label for="$id">$label</label>
       |${appendErrors(inputHtml, errors) }
       |</div>
    """.stripMargin
  }
}
