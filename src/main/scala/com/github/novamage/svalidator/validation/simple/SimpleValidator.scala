package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation._

import scala.collection.GenTraversableOnce

abstract class SimpleValidator[A] extends IValidate[A] {


  def WithRules(ruleBuilders: IRuleBuilder[A]*)(implicit instance: A): ValidationSummary = {
    val ruleStreamCollections = ruleBuilders.toList.map(_.buildRules(instance))
    val results = ruleStreamCollections.flatMap {
      collection => processRuleStreamCollection(instance, collection)
    }
    ValidationSummary(results)
  }

  def processRuleStreamCollection(instance: A, collection: RuleStreamCollection[A]): List[ValidationFailure] = {
    collection.chains.flatMap { chain =>
      val upstreamResults = chain.dependsOnUpstream.map(processRuleStreamCollection(instance, _)).getOrElse(Nil)
      if (upstreamResults.isEmpty) {
        chain.mainStream.flatMap {
          ruleStream =>
            ruleStream map { _.apply(instance) } collectFirst { case result if result.nonEmpty => result }
        }.flatten
      } else {
        upstreamResults
      }
    }
  }

  def When(conditionalExpression: A => Boolean): ConditionedGroupValidationRuleBuilder[A] = {
    new ConditionedGroupValidationRuleBuilder(conditionalExpression)
  }

  def For[B](propertyExpression: A => B): FieldListRequiringSimpleValidatorRuleBuilder[A, B] = {
    val composedFunction: (A => List[B]) = x => propertyExpression(x) :: Nil
    new FieldListRequiringSimpleValidatorRuleBuilder[A, B](composedFunction, false)
  }

  def ForEach[B](propertyListExpression: A => List[B]): FieldListRequiringSimpleValidatorRuleBuilder[A, B] = {
    new FieldListRequiringSimpleValidatorRuleBuilder[A, B](propertyListExpression, true)
  }

  def ForOptional[B](optionalPropertyExpression: A => Option[B]): FieldListRequiringSimpleValidatorRuleBuilder[A, B] = {
    val composedFunction: (A => List[B]) = x => optionalPropertyExpression(x).toList
    new FieldListRequiringSimpleValidatorRuleBuilder[A, B](composedFunction, false)
  }

  def ForComponent[B](componentPropertyExpression: A => B): ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, B] = {
    val composedFunction: (A => List[B]) = x => componentPropertyExpression(x) :: Nil
    new ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, B](composedFunction, false)
  }

  def ForEachComponent[B](componentListPropertyExpression: A => List[B]): ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, B] = {
    new ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, B](componentListPropertyExpression, true)
  }

  def ForOptionalComponent[B](optionalComponentPropertyExpression: A => Option[B]): ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, B] = {
    val composedFunction: (A => List[B]) = x => optionalComponentPropertyExpression(x).toList
    new ComponentListFieldRequiringSimpleValidatorRuleBuilder[A, B](composedFunction, false)
  }

}

