import io.circe.parser
import io.circe.generic.auto._, io.circe.syntax._

case class Test(a: BigDecimal)


val test = Test(BigDecimal("5"))


val serialized = test.asJson


serialized.hcursor.downField("a").as[BigDecimal]

