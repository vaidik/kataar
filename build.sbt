lazy val root = (project in file(".")).
  settings(
    name := "karkhana",
    version := "0.1",
    scalaVersion := "2.11.2",
    libraryDependencies += "com.typesafe" % "config" % "1.2.1",
    libraryDependencies += "org.scala-lang.modules" %% "scala-pickling" % "0.10.0"
  )
