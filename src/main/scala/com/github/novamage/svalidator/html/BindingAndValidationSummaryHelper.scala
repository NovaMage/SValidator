package com.github.novamage.svalidator.html

import com.github.novamage.svalidator.validation.binding.BindingAndValidationSummary
import language.implicitConversions

/** Helper class that eases the use of an [[com.github.novamage.svalidator.html.HtmlFactory HtmlFactory]]
  *
  * @param summary The summary whose fields will be extracted to generate html inputs for
  * @tparam A Type of the instance validated by the summary
  */
class BindingAndValidationSummaryHelper[A](summary: BindingAndValidationSummary[A]) {

  /** Generates a &lt;input type="hidden"&gt; with the given name
    *
    * @param name Name attribute for the generated hidden input
    * @param valueGetter Value provider from the summary for the property of this hidden input
    * @param attributes Any additional attributes to put on the hidden input
    * @tparam B Type of the instance validated by the summary
    * @return The markup of the hidden input after applying the converter function
    */
  def hidden[B](name: String,
                valueGetter: A => Any,
                attributes: Map[String, Any] = Map.empty)(implicit htmlFactory: HtmlFactory[B]): B = {
    htmlFactory.hidden(summary, name, valueGetter, attributes)
  }

  /** Generates a &lt;input type="text"&gt; with the given name and label
    *
    * @param name Name attribute for the generated input
    * @param valueGetter Value provider from the summary for the property of this input
    * @param label Label to place alongside this input
    * @param attributes Any additional attributes to put on the input
    * @tparam B Type of the instance validated by the summary
    * @return The markup of the input after applying the converter function
    */
  def textBox[B](name: String,
                 valueGetter: A => Any,
                 label: String,
                 attributes: Map[String, Any] = Map.empty)(implicit htmlFactory: HtmlFactory[B]): B = {
    htmlFactory.textBox(summary, name, valueGetter, label, attributes)
  }

  /** Generates a &lt;input type="password"&gt; with the given name and label
    *
    * @param name Name attribute for the generated input
    * @param label Label to place alongside this input
    * @param attributes Any additional attributes to put on the input
    * @tparam B Type of the instance validated by the summary
    * @return The markup of the input after applying the converter function
    */
  def password[B](name: String,
                  label: String,
                  attributes: Map[String, Any] = Map.empty)(implicit htmlFactory: HtmlFactory[B]): B = {
    htmlFactory.password(summary, name, label, attributes)
  }

  /** Generates a &lt;input type="checkbox"&gt; with the given name and label
    *
    * @param name Name attribute for the generated input
    * @param valueGetter Value provider from the summary for the property of this input
    * @param label Label to place alongside this input
    * @param attributes Any additional attributes to put on the input
    * @tparam B Type of the instance validated by the summary
    * @return The markup of the input after applying the converter function
    */
  def checkBox[B](name: String,
                  valueGetter: A => Boolean,
                  label: String,
                  attributes: Map[String, Any] = Map.empty)(implicit htmlFactory: HtmlFactory[B]): B = {
    htmlFactory.checkBox(summary, name, valueGetter, label, attributes)
  }

  /** Generates a &lt;select&gt; with the given name and label
    *
    * @param name Name attribute for the generated input
    * @param valueGetter Value provider from the summary for the property of this input
    * @param optionValuesAndText List of options to display. First attribute will be used as the value, second attribute as the text
    * @param label Label to place alongside this input
    * @param attributes Any additional attributes to put on the input
    * @tparam B Type of the instance validated by the summary
    * @return The markup of the input after applying the converter function
    */
  def select[B](name: String,
                valueGetter: A => Any,
                optionValuesAndText: List[(Any, Any)],
                label: String,
                attributes: Map[String, Any] = Map.empty)(implicit htmlFactory: HtmlFactory[B]): B = {
    htmlFactory.select(summary, name, valueGetter, optionValuesAndText, label, attributes)
  }

  /** Generates a group of &lt;input type="radio"&gt; that share the given name, and are tagged as a group with the label
    *
    * @param name Name attribute for the group of inputs
    * @param valueGetter Value provider from the summary for the property of this input
    * @param optionValuesAndText List of radios to display. First attribute will be used as the value, second attribute as the label
    * @param label Label to place alongside the group of inputs
    * @param attributes Any additional attributes to pass to the group of inputs
    * @tparam B Type of the instance validated by the summary
    * @return The markup of the group of inputs after applying the converter function
    */
  def radioGroup[B](name: String,
                    valueGetter: A => Any,
                    optionValuesAndText: List[(Any, Any)],
                    label: String,
                    attributes: Map[String, Any] = Map.empty)(implicit htmlFactory: HtmlFactory[B]): B = {
    htmlFactory.radioGroup(summary, name, valueGetter, optionValuesAndText, label, attributes)
  }

  /** Generates a group of &lt;input type="checkbox"&gt; that share the given name, and are tagged as a group with the label
    *
    * @param name Name attribute for the group of inputs
    * @param valueGetter Values provider from the summary for the property of this input
    * @param optionValuesAndText List of checkboxes to display. First attribute will be used as the value, second attribute as the label
    * @param label Label to place alongside the group of inputs
    * @param attributes Any additional attributes to pass to the group of inputs
    * @tparam B Type of the instance validated by the summary
    * @return The markup of the group of inputs after applying the converter function
    */
  def checkBoxGroup[B](name: String,
                       valueGetter: A => List[Any],
                       optionValuesAndText: List[(Any, Any)],
                       label: String,
                       attributes: Map[String, Any] = Map.empty)(implicit htmlFactory: HtmlFactory[B]): B = {
    htmlFactory.checkBoxGroup(summary, name, valueGetter, optionValuesAndText, label, attributes)
  }

  /** Generates a &lt;textarea&gt; with the given name and label
    *
    * @param name Name attribute for the generated textarea
    * @param valueGetter Value provider from the summary for the property of this textarea
    * @param label Label to place alongside this textarea
    * @param attributes Any additional attributes to put on the textarea
    * @tparam B Type of the instance validated by the summary
    * @return The markup of the input after applying the converter function
    */
  def textArea[B](name: String,
                  valueGetter: A => Any,
                  label: String,
                  attributes: Map[String, Any] = Map.empty)(implicit htmlFactory: HtmlFactory[B]): B = {
    htmlFactory.textArea(summary, name, valueGetter, label, attributes)
  }


  /** Generates a &lt;button&gt; with the given name and displayed text
    *
    * @param name Name attribute for the generated button
    * @param text Text to display on the button
    * @param attributes Any additional attributes to put on the button
    * @tparam B Type of the instance validated by the summary
    * @return The markup of the input after applying the converter function
    */
  def button[B](name: String,
                text: String,
                attributes: Map[String, Any] = Map.empty)(implicit htmlFactory: HtmlFactory[B]): B = {
    htmlFactory.button(summary, name, text, attributes)
  }

  /** Generates a &lt;input type="submit"&gt; with the given name and value
    *
    * @param name Name attribute for the generated button
    * @param value Value attribute, which is the text displayed for the button
    * @param attributes Any additional attributes to put on the button
    * @tparam B Type of the instance validated by the summary
    * @return The markup of the input after applying the converter function
    */
  def submit[B](name: String,
                value: String,
                attributes: Map[String, Any] = Map.empty)(implicit htmlFactory: HtmlFactory[B]): B = {
    htmlFactory.submit(summary, name, value, attributes)
  }

}

/** Helper object that eases the use of an [[com.github.novamage.svalidator.html.HtmlFactory HtmlFactory]]
  */
object BindingAndValidationSummaryHelper {

  /** This method enables implicit conversions of summaries to add to them the HTML helper methods of an
    * [[com.github.novamage.svalidator.html.HtmlFactory HtmlFactory]].
    *
    * @param summary Summary to add the methods to
    * @tparam A Type of the instance validated for the summary
    * @return The wrapped summary with helper methods
    */
  implicit def helper[A](summary: BindingAndValidationSummary[A]): BindingAndValidationSummaryHelper[A] = {
    new BindingAndValidationSummaryHelper(summary)
  }

}

