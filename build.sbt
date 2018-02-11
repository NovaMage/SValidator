name := "SValidator"

version := "0.6.4"

scalaVersion := "2.12.4"

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")

exportJars := true

parallelExecution in Test := false

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.12.4"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

libraryDependencies += "org.mockito" % "mockito-all" % "1.10.19" % "test"
