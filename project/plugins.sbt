logLevel := sbt.Level.Warn

addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.12")
addSbtPlugin("org.wartremover" % "sbt-wartremover" % "3.1.5")

addSbtPlugin("org.typelevel" % "sbt-tpolecat" % "0.5.0")

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix"  % "0.11.1")
addSbtPlugin("org.scalameta" % "sbt-scalafmt"  % "2.5.2")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "2.0.9")
addSbtPlugin("org.scalameta" % "sbt-mdoc"      % "2.5.1")
addSbtPlugin("io.kevinlee"   % "sbt-docusaur"  % "0.16.0")

addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % "1.13.1")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.2.0")

val sbtDevOopsVersion = "3.1.0"
addSbtPlugin("io.kevinlee" % "sbt-devoops-scala"     % sbtDevOopsVersion)
addSbtPlugin("io.kevinlee" % "sbt-devoops-sbt-extra" % sbtDevOopsVersion)
addSbtPlugin("io.kevinlee" % "sbt-devoops-github"    % sbtDevOopsVersion)

addSbtPlugin("io.kevinlee" % "sbt-devoops-starter"    % sbtDevOopsVersion)

addSbtPlugin("org.jetbrains.scala" % "sbt-idea-compiler-indices" % "1.0.14")
