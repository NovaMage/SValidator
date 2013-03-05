package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.IRuleBuilder
import com.github.novamage.svalidator.validation.IValidationRule
package object constructs {

  import com.github.novamage.svalidator.validation.simple.SimpleValidationRuleBuilder

  def be = new BeConstruct

  def have = new HaveConstruct

  implicit class SimpleValidationRuleBuilderConstructExtensions[A, B](builder: SimpleValidationRuleBuilder[A, B]) {

    def must(beConstruct: BeConstruct) = {
      beConstruct.prepareConstructWithRule(builder)
    }

    def must(haveConstruct: HaveConstruct) = {
      haveConstruct.prepareConstructWithRule(builder)
    }
    
    private def applyNotFunctor(expression: B => Boolean) = {
        val notFunctor: (B => Boolean) => (B => Boolean) = originalExpression => parameter => !originalExpression(parameter)
        notFunctor(expression)
    }

    def mustNot(ruleExpression: B => Boolean) = {
      builder.must(applyNotFunctor(ruleExpression))
    }

    def mustNot(beConstruct: BeConstruct) = {
      val negatedBuilder = new NegatedSimpleValidationRuleBuilder(builder)
      beConstruct.prepareConstructWithRule(builder)
    }

    def mustNot(haveConstruct: HaveConstruct) = {
      val negatedBuilder = new NegatedSimpleValidationRuleBuilder(builder)
      haveConstruct.prepareConstructWithRule(builder)
    }

    private class NegatedSimpleValidationRuleBuilder(builder: SimpleValidationRuleBuilder[A, B]) extends SimpleValidationRuleBuilder[A, B](null, null, null, null, null) {

      protected[validation] override def must(ruleExpression: B => Boolean) = {
        builder.must(applyNotFunctor(ruleExpression))
      }

    }
  }

  class BeConstruct {
    def prepareConstructWithRule[A, B](builder: SimpleValidationRuleBuilder[A, B]) = {
      new BeConstructWithRuleBuilder(builder)
    }
  }

  class HaveConstruct {

    def prepareConstructWithRule[A, B](builder: SimpleValidationRuleBuilder[A, B]) = {
      new HaveConstructWithRuleBuilder(builder)
    }
  }

  class BeConstructWithRuleBuilder[A, B](protected[constructs] val builder: SimpleValidationRuleBuilder[A, B]) {

  }

  class HaveConstructWithRuleBuilder[A, B](protected[constructs] val builder: SimpleValidationRuleBuilder[A, B]) {

  }

  implicit class BeConstructWithRuleBuilderForStringExtensions[A](construct: BeConstructWithRuleBuilder[A, String]) {

    def empty() = {
      construct.builder must { _.length == 0 }
    }

  }

  implicit class HaveConstructWithRuleBuilderForStringExtensions[A](construct: HaveConstructWithRuleBuilder[A, String]) {

    def maxLength(maxLength: Int) = {
      construct.builder must { _.length <= maxLength }
    }

    def minLength(minLength: Int) = {
      construct.builder must { _.length >= minLength }
    }
  }

}
