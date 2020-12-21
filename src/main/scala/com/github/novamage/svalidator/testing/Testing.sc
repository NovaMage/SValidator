import io.circe.parser

val jsonObject = parser.parse("{\"juana\":[18]}")
jsonObject match {
  case Left(value) =>
    println(value)
  case Right(value) =>
    println("The number is:" + value.hcursor.downField("juana").downArray.succeeded)
}
