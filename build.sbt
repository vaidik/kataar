lazy val root = (project in file(".")).
  settings(
    name := "karkhana",
    version := "0.1",
    scalaVersion := "2.11.2",
    libraryDependencies += "com.typesafe" % "config" % "1.2.1"
  )
