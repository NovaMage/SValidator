package com.github.novamage.svalidator.utils

import testUtils.Observes

sealed abstract case class ATypeBasedEnum(id: Int, description: String, somethingElse: Any) extends ATypeBasedEnum.Value

object ATypeBasedEnum extends TypeBasedEnumeration[ATypeBasedEnum] {

  object TypeBasedFirstOption extends ATypeBasedEnum(1, "The first typed option", "anything4")

  object TypeBasedSecondOption extends ATypeBasedEnum(2, "The second typed option", BigDecimal("390"))

  object TypeBasedThirdOption extends ATypeBasedEnum(3, "The third typed option", true)

}

class TypeBasedEnumerationSpecs extends Observes {

  describe("when getting the list of values for a type based enumartion") {

    val values = ATypeBasedEnum.values

    it("should contain all the values for the enumartion"){
      values should contain(ATypeBasedEnum.TypeBasedFirstOption)
      values should contain(ATypeBasedEnum.TypeBasedSecondOption)
      values should contain(ATypeBasedEnum.TypeBasedThirdOption)
      values should have size 3
    }

  }

}
