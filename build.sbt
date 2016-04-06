name := "Estimates Questions on Notice Feed"

version := "1.0"

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-unchecked", "-deprecation")

coverageEnabled := true

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",

  "org.slf4j" % "slf4j-simple" % "1.7.19",

  "net.ruippeixotog" %% "scala-scraper" % "0.1.1",

  "com.typesafe.akka" %% "akka-actor" % "2.3.12",
  "com.typesafe.slick" %% "slick" % "3.1.0",
  "org.xerial" % "sqlite-jdbc" % "3.8.11.2",

  "commons-io" % "commons-io" % "2.4"
)