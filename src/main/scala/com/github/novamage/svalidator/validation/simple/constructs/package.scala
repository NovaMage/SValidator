package com.github.novamage.svalidator.validation.simple


package object constructs {


  def be = new BeConstruct

  def be[A](value: A): (A => Boolean) = _ == value

  def have = new HaveConstruct

  def equal[A](value: A): (A => Boolean) = _ == value

  implicit class SimpleValidationRuleBuilderConstructExtensions[A, B, C](builder: SimpleListValidationRuleStarterBuilder[A, B]) {

    def must(beConstruct: BeConstruct): BeConstructWithRuleBuilder[A, B] = {
      beConstruct.prepareConstructWithRule(builder)
    }

    def must(haveConstruct: HaveConstruct): HaveConstructWithRuleBuilder[A, B] = {
      haveConstruct.prepareConstructWithRule(builder)
    }

    def mustNot(beConstruct: BeConstruct): BeConstructWithRuleBuilder[A, B] = {
      val negatedBuilder = new NegatedAbstractValidationRuleStarterBuilder(builder)
      beConstruct.prepareConstructWithRule(negatedBuilder)
    }

    def mustNot(haveConstruct: HaveConstruct): HaveConstructWithRuleBuilder[A, B] = {
      val negatedBuilder = new NegatedAbstractValidationRuleStarterBuilder(builder)
      haveConstruct.prepareConstructWithRule(negatedBuilder)
    }

    private class NegatedAbstractValidationRuleStarterBuilder(builder: SimpleListValidationRuleStarterBuilder[A, B]) extends SimpleListValidationRuleStarterBuilder[A, B](null, null, null, null, false, null) {

      override def must(ruleExpression: (B) => Boolean): SimpleListValidationRuleContinuationBuilder[A, B] = builder.mustNot(ruleExpression)

      override def mustNot(ruleExpression: (B) => Boolean): SimpleListValidationRuleContinuationBuilder[A, B] = builder.must(ruleExpression)

    }

  }

  class BeConstruct {
    def prepareConstructWithRule[A, B, C](builder: SimpleListValidationRuleStarterBuilder[A, B]): BeConstructWithRuleBuilder[A, B] = {
      new BeConstructWithRuleBuilder(builder)
    }
  }

  class HaveConstruct {

    def prepareConstructWithRule[A, B, C](builder: SimpleListValidationRuleStarterBuilder[A, B]): HaveConstructWithRuleBuilder[A, B] = {
      new HaveConstructWithRuleBuilder(builder)
    }
  }

  class BeConstructWithRuleBuilder[A, B](protected[constructs] val builder: SimpleListValidationRuleStarterBuilder[A, B]) {

  }

  class HaveConstructWithRuleBuilder[A, B](protected[constructs] val builder: SimpleListValidationRuleStarterBuilder[A, B]) {

  }

  implicit class BeConstructWithRuleBuilderForStringExtensions[A, B](construct: BeConstructWithRuleBuilder[A, String]) {

    def empty(): SimpleListValidationRuleContinuationBuilder[A, String] = {
      construct.builder must { x => x == null || x.length == 0 }
    }

    def trimmed(): SimpleListValidationRuleContinuationBuilder[A, String] = {
      construct.builder must { x => x.trim.length == x.length }
    }

  }

  implicit class BeConstructWithRuleBuilderForIterableExtensions[A, B](construct: BeConstructWithRuleBuilder[A, B])(implicit evidence: B => Iterable[_]) {

    def empty(): SimpleListValidationRuleContinuationBuilder[A, B] = {
      construct.builder must { _.isEmpty }
    }

  }

  implicit class BeConstructWithRuleBuilderForNumbersExtensions[A, B](construct: BeConstructWithRuleBuilder[A, B])(implicit evidence: B => Double) {

    def negative(): SimpleListValidationRuleContinuationBuilder[A, B] = {
      construct.builder must { _ < 0 }
    }

    def positive(): SimpleListValidationRuleContinuationBuilder[A, B] = {
      construct.builder must { _ > 0 }
    }

    def greaterThan(value: B): SimpleListValidationRuleContinuationBuilder[A, B] = {
      construct.builder must { _ > value }
    }

    def lessThan(value: B): SimpleListValidationRuleContinuationBuilder[A, B] = {
      construct.builder must { _ < value }
    }

  }

  implicit class HaveConstructWithRuleBuilderForStringExtensions[A, B](construct: HaveConstructWithRuleBuilder[A, String]) {

    def maxLength(maxLength: Int): SimpleListValidationRuleContinuationBuilder[A, String] = {
      construct.builder must { _.length <= maxLength }
    }

    def minLength(minLength: Int): SimpleListValidationRuleContinuationBuilder[A, String] = {
      construct.builder must { _.length >= minLength }
    }

    def length(targetLength: Int): SimpleListValidationRuleContinuationBuilder[A, String] = {
      construct.builder must { _.length == targetLength }
    }
  }

  implicit class HaveConstructWithRuleBuilderForIterableExtensions[A, B](construct: HaveConstructWithRuleBuilder[A, B])(implicit evidence: B => Iterable[_]) {

    def maxSize(maxSize: Int): SimpleListValidationRuleContinuationBuilder[A, B] = {
      construct.builder must { _.size <= maxSize }
    }

    def minSize(minSize: Int): SimpleListValidationRuleContinuationBuilder[A, B] = {
      construct.builder must { _.size >= minSize }
    }

    def size(targetSize: Int): SimpleListValidationRuleContinuationBuilder[A, B] = {
      construct.builder must { _.size == targetSize }
    }
  }

}
