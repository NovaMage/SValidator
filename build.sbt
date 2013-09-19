name := "SValidator"

version := "0.2.2"

scalaVersion := "2.10.2"

scalacOptions += "-feature"

mainClass := Some("com.github.novamage.svalidator.Main")

exportJars := true

parallelExecution in Test := false

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.10.2"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0.M5b" % "test"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0.M5b" % "test"

libraryDependencies += "junit" % "junit" % "4.6" % "test"

libraryDependencies += "org.mockito" % "mockito-all" % "1.9.5" % "test"
