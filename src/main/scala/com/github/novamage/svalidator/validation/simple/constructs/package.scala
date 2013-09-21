package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.IValidationRule


package object constructs {

  import com.github.novamage.svalidator.validation.simple.AbstractValidationRuleBuilder

  def be = new BeConstruct

  def be[A](value: A): (A => Boolean) = _ == value

  def have = new HaveConstruct

  def equal[A](value: A): (A => Boolean) = _ == value

  implicit class SimpleValidationRuleBuilderConstructExtensions[A, B, C](builder: AbstractValidationRuleBuilder[A, B, C]) {

    def must(beConstruct: BeConstruct) = {
      beConstruct.prepareConstructWithRule(builder)
    }

    def must(haveConstruct: HaveConstruct) = {
      haveConstruct.prepareConstructWithRule(builder)
    }

    def mustNot(beConstruct: BeConstruct) = {
      val negatedBuilder = new NegatedAbstractValidationRuleBuilder(builder)
      beConstruct.prepareConstructWithRule(negatedBuilder)
    }

    def mustNot(haveConstruct: HaveConstruct) = {
      val negatedBuilder = new NegatedAbstractValidationRuleBuilder(builder)
      haveConstruct.prepareConstructWithRule(negatedBuilder)
    }

    private class NegatedAbstractValidationRuleBuilder(builder: AbstractValidationRuleBuilder[A, B, C]) extends AbstractValidationRuleBuilder[A, B, C](null, null, null, null, null) {

      override def must(ruleExpression: (C) => Boolean): AbstractValidationRuleBuilder[A, B, C] = builder.mustNot(ruleExpression)

      override def mustNot(ruleExpression: (C) => Boolean): AbstractValidationRuleBuilder[A, B, C] = builder.must(ruleExpression)

      override def mustComply(ruleExpression: (C, A) => Boolean): AbstractValidationRuleBuilder[A, B, C] = builder.mustNotComply(ruleExpression)

      override def mustNotComply(ruleExpression: (C, A) => Boolean): AbstractValidationRuleBuilder[A, B, C] = builder.mustComply(ruleExpression)

      protected[validation] def processRuleStructures(instance: A, ruleStructuresList: List[SimpleValidationRuleStructureContainer[A, C]]): Stream[IValidationRule[A]] = ???

      protected[validation] def buildNextInstanceInChain(propertyExpression: (A) => B, currentRuleStructure: SimpleValidationRuleStructureContainer[A, C], validationExpressions: List[SimpleValidationRuleStructureContainer[A, C]], fieldName: String, previousMappedBuilder: Option[AbstractValidationRuleBuilder[A, _, _]]) = ???
    }

  }

  class BeConstruct {
    def prepareConstructWithRule[A, B, C](builder: AbstractValidationRuleBuilder[A, B, C]) = {
      new BeConstructWithRuleBuilder(builder)
    }
  }

  class HaveConstruct {

    def prepareConstructWithRule[A, B, C](builder: AbstractValidationRuleBuilder[A, B, C]) = {
      new HaveConstructWithRuleBuilder(builder)
    }
  }

  class BeConstructWithRuleBuilder[A, B, C](protected[constructs] val builder: AbstractValidationRuleBuilder[A, B, C]) {

  }

  class HaveConstructWithRuleBuilder[A, B, C](protected[constructs] val builder: AbstractValidationRuleBuilder[A, B, C]) {

  }

  implicit class BeConstructWithRuleBuilderForStringExtensions[A, B](construct: BeConstructWithRuleBuilder[A, B, String]) {

    def empty() = {
      construct.builder must {x => x == null || x.length == 0}
    }

  }

  implicit class BeConstructWithRuleBuilderForNumbersExtensions[A, B, C <% Double](construct: BeConstructWithRuleBuilder[A, B, C]) {

    def negative() = {
      construct.builder must {_ < 0}
    }

    def positive() = {
      construct.builder must {_ > 0}
    }

    def greaterThan(value: C) = {
      construct.builder must {_ > value}
    }

    def lessThan(value: C) = {
      construct.builder must {_ < value}
    }

  }

  implicit class HaveConstructWithRuleBuilderForStringExtensions[A, B](construct: HaveConstructWithRuleBuilder[A, B, String]) {

    def maxLength(maxLength: Int) = {
      construct.builder must {_.length <= maxLength}
    }

    def minLength(minLength: Int) = {
      construct.builder must {_.length >= minLength}
    }
  }

}
