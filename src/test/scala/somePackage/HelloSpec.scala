package somePackage

import testUtils.Observes

class HelloSpec extends Observes {

  val sut : IncreasesNumbers = Hello

  describe("when increasing the number 4 by one"){

    val result = sut.increase(4)

    it("should have returned 5"){
      result should equal(5)
    }

  }

  describe("when increasing the number 28 by one"){

    val result = sut.increase(28)

    it("should have returned 29"){
      result should equal(29)
    }

  }

}
