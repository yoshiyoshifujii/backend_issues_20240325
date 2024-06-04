lazy val PekkoVersion     = "1.0.2"
lazy val PekkoHttpVersion = "1.0.1"
lazy val DoobieVersion    = "1.0.0-RC5"

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name         := "school-api-pekko",
    organization := "com.github.yoshiyoshifujii",
    scalaVersion := "2.13.14",
    version      := "0.1.0",
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-actor-typed"     % PekkoVersion,
      "org.apache.pekko" %% "pekko-stream"          % PekkoVersion,
      "org.apache.pekko" %% "pekko-slf4j"           % PekkoVersion,
      "org.apache.pekko" %% "pekko-http"            % PekkoHttpVersion,
      "org.apache.pekko" %% "pekko-http-spray-json" % PekkoHttpVersion,
      "ch.qos.logback"    % "logback-classic"       % "1.5.6",
      "com.mysql"         % "mysql-connector-j"     % "8.4.0",
      "org.tpolecat"     %% "doobie-core"           % DoobieVersion
    ),
    libraryDependencies ++= Seq(
      "org.scalatest"     %% "scalatest"            % "3.2.18"         % Test,
      "org.scalatestplus" %% "mockito-5-10"         % "3.2.18.0"       % Test,
      "org.apache.pekko"  %% "pekko-stream-testkit" % PekkoVersion     % Test,
      "org.apache.pekko"  %% "pekko-http-testkit"   % PekkoHttpVersion % Test,
      "org.tpolecat"      %% "doobie-scalatest"     % DoobieVersion    % Test
    )
  )
  .settings(
    dockerBaseImage              := "eclipse-temurin:17.0.11_9-jre",
    dockerBuildxPlatforms        := Seq("linux/amd64"),
    packageDoc / publishArtifact := false,
    dockerExposedPorts ++= Seq(8080),
    bashScriptExtraDefines ++= Seq(
      "addJava -Xms${JVM_HEAP_MIN:-1024m}",
      "addJava -Xmx${JVM_HEAP_MAX:-4096m}",
      "addJava -XX:MaxMetaspaceSize=${JVM_META_MAX:-512M}",
      "addJava ${JVM_GC_OPTIONS:--XX:+UseG1GC}"
    )
  )
