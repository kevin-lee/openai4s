import sbtcrossproject.CrossProject

ThisBuild / scalaVersion := props.ProjectScalaVersion
ThisBuild / organization := props.Org
ThisBuild / organizationName := "Kevin's Code"
ThisBuild / crossScalaVersions := props.CrossScalaVersions

ThisBuild / testFrameworks ~=
  (frameworks => (TestFramework("hedgehog.sbt.Framework") +: frameworks).distinct)

ThisBuild / developers := List(
  Developer(
    props.GitHubUsername,
    "Kevin Lee",
    "kevin.code@kevinlee.io",
    url(s"https://github.com/${props.GitHubUsername}"),
  )
)

ThisBuild / homepage := Some(url(s"https://github.com/${props.GitHubUsername}/${props.RepoName}"))
ThisBuild / scmInfo :=
  Some(
    ScmInfo(
      url(s"https://github.com/${props.GitHubUsername}/${props.RepoName}"),
      s"git@github.com:${props.GitHubUsername}/${props.RepoName}.git",
    )
  )
ThisBuild / licenses := props.licenses

ThisBuild / resolvers += props.SonatypeSnapshots

ThisBuild / scalafixConfig := (
  if (scalaVersion.value.startsWith("3"))
    ((ThisBuild / baseDirectory).value / ".scalafix-scala3.conf").some
  else
    ((ThisBuild / baseDirectory).value / ".scalafix-scala2.conf").some
)

lazy val openai4s = (project in file("."))
  .enablePlugins(DevOopsGitHubReleasePlugin)
  .settings(
    name := prefixedProjectName(""),
    description := "openai4s - OpenAI Client for Scala",
    libraryDependencies :=
      libraryDependenciesPostProcess(isScala3(scalaVersion.value), libraryDependencies.value),
  )
  .settings(noPublish)
  .settings(mavenCentralPublishSettings)

lazy val props =
  new {

    val Org = "io.kevinlee"

    val GitHubUsername = "Kevin-Lee"
    val RepoName       = "openai4s"

    val Scala2Versions = List(
      "2.13.10",
      "2.12.17",
    )
    val Scala2Version  = Scala2Versions.head

    val Scala3Version = "3.1.3"

//    val ProjectScalaVersion = "2.13.10"
    val ProjectScalaVersion = Scala2Version
//    val ProjectScalaVersion = Scala3Version

    lazy val licenses = List("MIT" -> url("http://opensource.org/licenses/MIT"))

    val SonatypeCredentialHost = "s01.oss.sonatype.org"
    val SonatypeRepository     = s"https://$SonatypeCredentialHost/service/local"

    val SonatypeSnapshots = "sonatype-snapshots" at s"https://$SonatypeCredentialHost/content/repositories/snapshots"

    val removeDottyIncompatible: ModuleID => Boolean =
      m =>
          m.name == "ammonite" ||
          m.name == "kind-projector" ||
          m.name == "better-monadic-for" ||
          m.name == "mdoc"

    val CrossScalaVersions =
      (Scala3Version :: Scala2Versions).distinct

    val IncludeTest = "compile->compile;test->test"

    val HedgehogVersion = "0.10.1"

    val CatsVersion = "2.7.0"

    val CatsEffect2Version       = "2.4.1"
    val CatsEffect2LatestVersion = "2.5.4"
    val CatsEffect3Version       = "3.3.14"

    val ExtrasVersion = "0.38.0"

  }

lazy val libs = new {
  lazy val hedgehogCore   = "qa.hedgehog" %% "hedgehog-core"   % props.HedgehogVersion
  lazy val hedgehogRunner = "qa.hedgehog" %% "hedgehog-runner" % props.HedgehogVersion
  lazy val hedgehogSbt    = "qa.hedgehog" %% "hedgehog-sbt"    % props.HedgehogVersion

  lazy val hedgehog: List[ModuleID] =
    List(
      hedgehogCore,
      hedgehogRunner,
      hedgehogSbt,
    ).map(_ % Test)

}

lazy val mavenCentralPublishSettings: SettingsDefinition = List(
  /* Publish to Maven Central { */
  sonatypeCredentialHost := props.SonatypeCredentialHost,
  sonatypeRepository := props.SonatypeRepository,
  /* } Publish to Maven Central */
)

// scalafmt: off
def prefixedProjectName(name: String) = s"${props.RepoName}${if (name.isEmpty) "" else s"-$name"}"
// scalafmt: on

def isScala3(scalaVersion: String): Boolean = scalaVersion.startsWith("3")

def libraryDependenciesPostProcess(
  isDotty: Boolean,
  libraries: Seq[ModuleID],
): Seq[ModuleID] =
  if (isDotty)
    libraries.filterNot(props.removeDottyIncompatible)
  else
    libraries


def module(projectName: String, crossProject: CrossProject.Builder): CrossProject = {
  val prefixedName = prefixedProjectName(projectName)
  crossProject
    .in(file(s"modules/$prefixedName"))
    .settings(
      name := prefixedName,
      fork := true,
      scalafixConfig := (
        if (scalaVersion.value.startsWith("3"))
          ((ThisBuild / baseDirectory).value / ".scalafix-scala3.conf").some
        else
          ((ThisBuild / baseDirectory).value / ".scalafix-scala2.conf").some
      ),
      scalacOptions ++= (if (isScala3(scalaVersion.value)) List.empty else List("-Xsource:3")),
      libraryDependencies ++= libs.hedgehog,
      wartremoverErrors ++= Warts.allBut(Wart.Any, Wart.Nothing),
      Compile / console / scalacOptions :=
        (console / scalacOptions)
          .value
          .filterNot(option => option.contains("wartremover") || option.contains("import")),
      Test / console / scalacOptions :=
        (console / scalacOptions)
          .value
          .filterNot(option => option.contains("wartremover") || option.contains("import")),
      /* } WartRemover and scalacOptions */
      testFrameworks ++= (testFrameworks.value ++ Seq(TestFramework("hedgehog.sbt.Framework"))).distinct,
      licenses := props.licenses,
      /* coverage { */
      coverageHighlighting := (CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 10)) | Some((2, 11)) =>
          false
        case _ =>
          true
      }),
      /* } coverage */
    )
    .settings(mavenCentralPublishSettings)
}

lazy val jsSettingsForFuture: SettingsDefinition = List(
  Test / scalacOptions ++= (if (scalaVersion.value.startsWith("3")) List.empty
                            else List("-P:scalajs:nowarnGlobalExecutionContext")),
  Test / compile / scalacOptions ++= (if (scalaVersion.value.startsWith("3")) List.empty
                                      else List("-P:scalajs:nowarnGlobalExecutionContext")),
)
