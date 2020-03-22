name := "ZIOHttpExample"

version := "0.1"

scalaVersion := "2.13.1"

scalacOptions ++= Seq ("-unchecked","-deprecation","-encoding", "utf-8", "-feature", "-Xlint", "-Xfatal-warnings")

libraryDependencies ++= Seq(
  "dev.zio"                     %% "zio"                    % "1.0.0-RC18-2",
  "com.typesafe.akka"           %% "akka-http"              % "10.1.11",
  "com.typesafe.akka"           %% "akka-stream"            % "2.5.26"
)