name := "SValidator"

version := "0.5.0"

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-feature", "-Xfatal-warnings", "-unchecked", "-deprecation")

exportJars := true

parallelExecution in Test := false

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.11.7"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.5" % "test"

libraryDependencies += "org.mockito" % "mockito-all" % "1.10.19" % "test"
