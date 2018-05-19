organization := "com.github.novamage"

name := "SValidator"

description := "A library for validation of scala objects in a fluent and concise manner"

version := "0.9.3"

scalaVersion := "2.12.5"

licenses := Seq("MIT" -> url("https://github.com/NovaMage/SValidator/blob/master/LICENSE.txt"))

homepage := Some(url("https://github.com/NovaMage/SValidator"))

scmInfo := Some(ScmInfo(
  browseUrl = url("https://github.com/NovaMage/SValidator"),
  connection = "scm:git@github.com:NovaMage/SValidator.git")
)

developers := List(
  Developer(
    id = "NovaMage",
    name = "Ángel Felipe Blanco Guzmán",
    email = "angel.softworks@gmail.com",
    url = url("https://github.com/NovaMage")
  )
)

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

coverageEnabled := false

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")

exportJars := true

parallelExecution in Test := false

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.12.5"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

libraryDependencies += "org.mockito" % "mockito-all" % "1.10.19" % "test"

pomIncludeRepository := { _ => false }
