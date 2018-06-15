package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.simple.internals.{RuleBuilder, SimpleValidationRuleStructureContainer, UpstreamLazyValueProvider}

/** Builder that applies a boolean function to validate an extracted property
  *
  * @tparam A Type of the instance being validated
  * @tparam B Type of the extracted property being validated
  * @tparam C If the property has been mapped, type the property had before the map, otherwise, [[scala.Nothing Nothing]]
  */
class SimpleListValidationRuleStarterBuilder[A, B, +C](propertyListExpression: A => List[B],
                                                       currentRuleStructure: Option[SimpleValidationRuleStructureContainer[A, B]],
                                                       validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                                       fieldName: String,
                                                       markIndexesOfFieldNameErrors: Boolean,
                                                       previousMappedBuilderInChain: Option[RuleBuilder[A]],
                                                       previousMappedBuilderValueProvider: Option[UpstreamLazyValueProvider[List[C]]],
                                                       previousMappedBuilderValueConverter: Option[C => B]) {


  private lazy val notFunctor: ((B, A) => Boolean) => (B, A) => Boolean = originalExpression => (propertyValue, instanceValue) => !originalExpression(propertyValue, instanceValue)

  /** Returns an identical builder to this with its must and mustNot methods reversed.
    */
  protected[simple] def negated: SimpleListValidationRuleStarterBuilder[A, B, C] = {
    new SimpleListValidationRuleStarterBuilder(propertyListExpression,
      currentRuleStructure,
      validationExpressions,
      fieldName,
      markIndexesOfFieldNameErrors,
      previousMappedBuilderInChain,
      previousMappedBuilderValueProvider,
      previousMappedBuilderValueConverter) {

      override def must(ruleExpressionReceivingPropertyValue: B => Boolean): SimpleListValidationRuleContinuationBuilder[A, B, C] = {
        super.mustNot(ruleExpressionReceivingPropertyValue)
      }

      override def mustNot(ruleExpressionReceivingPropertyValue: B => Boolean): SimpleListValidationRuleContinuationBuilder[A, B, C] = {
        super.must(ruleExpressionReceivingPropertyValue)
      }

    }
  }

  /** Generates a rule that will cause a validation error if applying this function to the instance returns true
    *
    * @param ruleExpressionReceivingPropertyValue Expression to apply
    */
  def must(ruleExpressionReceivingPropertyValue: B => Boolean): SimpleListValidationRuleContinuationBuilder[A, B, C] = {
    val syntheticExpressionWithInstance: (B, A) => Boolean = (property, _) => ruleExpressionReceivingPropertyValue(property)
    addRuleExpressionToList(syntheticExpressionWithInstance)
  }

  /** Generates a rule that will cause a validation error if applying this function to the instance returns false
    *
    * @param ruleExpressionReceivingPropertyValue Expression to apply
    */
  def mustNot(ruleExpressionReceivingPropertyValue: B => Boolean): SimpleListValidationRuleContinuationBuilder[A, B, C] = {
    val syntheticExpressionWithInstance: (B, A) => Boolean = (property, _) => ruleExpressionReceivingPropertyValue(property)
    addRuleExpressionToList(applyNotFunctor(syntheticExpressionWithInstance))
  }

  protected[validation] def buildNextInstanceInChain(propertyExpression: A => List[B],
                                                     currentRuleStructure: Option[SimpleValidationRuleStructureContainer[A, B]],
                                                     validationExpressions: List[SimpleValidationRuleStructureContainer[A, B]],
                                                     fieldName: String): SimpleListValidationRuleContinuationBuilder[A, B, C] = {
    new SimpleListValidationRuleContinuationBuilder(propertyListExpression,
      currentRuleStructure,
      validationExpressions,
      fieldName,
      markIndexesOfFieldNameErrors,
      previousMappedBuilderInChain,
      previousMappedBuilderValueProvider,
      previousMappedBuilderValueConverter)
  }

  private def addRuleExpressionToList(ruleExpression: (B, A) => Boolean): SimpleListValidationRuleContinuationBuilder[A, B, C] = {
    val ruleList = currentRuleStructure match {
      case None => validationExpressions
      case Some(ruleStructure) => validationExpressions ::: ruleStructure :: Nil
    }
    buildNextInstanceInChain(propertyListExpression, Some(SimpleValidationRuleStructureContainer[A, B](ruleExpression, None, None, None, Map.empty)), ruleList, fieldName)
  }

  private def applyNotFunctor(expression: (B, A) => Boolean): (B, A) => Boolean = {
    notFunctor(expression)
  }

}
