import com.amazonaws.regions.Regions

/* =========================================================================================
 * Copyright © 2013-2019 the kamon project <http://kamon.io/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * =========================================================================================
 */

organization := "io.artos"

val baseVersion = "2.0.1-artos"

val buildVersion = {
  val branch = sys.env.get("BRANCH_NAME")
  lazy val snapshotVersion =
    s"$baseVersion-${branch.map(_ + "-").getOrElse("")}${sys.env.getOrElse("BUILD_NUMBER", "DEV")}-SNAPSHOT"

  branch.fold {
    println(
      "$BRANCH_NAME environment variable not defined, creating snapshot build"
    )
    snapshotVersion
  } { branchName =>
    val master = branch.contains("master")
    println(
      s"$$BRANCH_NAME environment set to $branchName, creating ${if (master) "release"
      else "snapshot"} build"
    )
    if (master) {
      baseVersion
    } else {
      snapshotVersion
    }
  }
}

version in ThisBuild := buildVersion

val kamonCore         = "io.kamon"    %% "kamon-core"                     % "2.0.4"
val kamonTestkit      = "io.kamon"    %% "kamon-testkit"                  % "2.0.4"
val kamonCommon       = "io.kamon"    %% "kamon-instrumentation-common"   % "2.0.1"

val server            = "org.http4s"  %%  "http4s-blaze-server"   % "0.20.15"
val client            = "org.http4s"  %%  "http4s-blaze-client"   % "0.20.15"
val dsl               = "org.http4s"  %%  "http4s-dsl"            % "0.20.15"

val kanela            = "io.kamon"    %  "kanela-agent"           % "1.0.0"

val awsRegion = "eu-west-2"
val s3BaseUrl = s"s3://s3-$awsRegion.amazonaws.com"

resolvers ++= Seq[Resolver](
  Resolver
    .url("Aventus Releases resolver", url(s"$s3BaseUrl/releases.repo.aventus.io"))(Resolver.ivyStylePatterns),
  Resolver
    .url("Aventus Snapshots resolver", url(s"$s3BaseUrl/snapshots.repo.aventus.io"))(Resolver.ivyStylePatterns),
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

lazy val root = (project in file("."))
  .settings(Seq(
      name := "kamon-http4s",
      scalaVersion := "2.12.6",
      crossScalaVersions := Seq("2.11.12", "2.12.8")))
//  .settings(resolvers += Resolver.bintrayRepo("kamon-io", "snapshots"))
//  .settings(resolvers += Resolver.mavenLocal)
  .settings(scalacOptions ++= Seq("-Ypartial-unification", "-language:higherKinds"))
  .settings(
    libraryDependencies ++=
      compileScope(kamonCore, kamonCommon) ++
      providedScope(server, client, dsl, kanela) ++
      testScope(scalatest, kamonTestkit, logbackClassic))

publishMavenStyle := false
publishTo := {
  val prefix = if (isSnapshot.value) "snapshots" else "releases"
  Some(
    Resolver
      .url(s"Aventus $prefix S3 bucket", url(s"$s3BaseUrl/$prefix.repo.aventus.io"))(Resolver.ivyStylePatterns)
  )
}
