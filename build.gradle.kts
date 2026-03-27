@file:OptIn(ExperimentalWasmDsl::class)

import com.ncorti.ktfmt.gradle.tasks.KtfmtCheckTask
import com.ncorti.ktfmt.gradle.tasks.KtfmtFormatTask
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.ktfmt)

  id("maven-publish")
}

repositories { mavenCentral() }

kotlin {
  explicitApi()

  sourceSets {
    all { languageSettings.optIn("kotlin.ExperimentalMultiplatform") }

    commonTest {
      dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
      }
    }

    jsTest { dependencies { implementation(kotlin("test-js")) } }
    jvmTest { dependencies { implementation(kotlin("test-junit")) } }

    wasmJsMain { kotlin.srcDir("src/nativeMain/kotlin") }
  }

  targets.all {
    compilations.all {
      compileTaskProvider.configure {
        compilerOptions { freeCompilerArgs.add("-Xexpect-actual-classes") }
      }
    }
  }

  jvm { compilerOptions { jvmTarget = JvmTarget.JVM_21 } }

  js(IR) {
    // browser()
    nodejs()
  }

  wasmJs { nodejs() }
  linuxX64()
  linuxArm64()
}

tasks.withType<KtfmtFormatTask> { exclude("**/generated/*.kt") }

tasks.withType<KtfmtCheckTask> { exclude("**/generated/*.kt") }

// region Code generation

val codegen by
    tasks.registering {
      val maxTupleSize = 16

      andCodegen(
          maxTupleSize,
          kotlin.sourceSets.commonMain.get().kotlin.srcDirs.first().absolutePath +
              "/generated/andFunctions.kt")
      tupleCodegen(
          maxTupleSize,
          kotlin.sourceSets.commonMain.get().kotlin.srcDirs.first().absolutePath +
              "/generated/tuples.kt")
    }

kotlin.sourceSets.commonMain { kotlin.srcDirs(files().builtBy(codegen)) }

// endregion

fun customizeForMavenCentral(pom: org.gradle.api.publish.maven.MavenPom) =
    pom.withXml {
      fun groovy.util.Node.add(key: String, value: String) {
        appendNode(key).setValue(value)
      }

      fun groovy.util.Node.node(key: String, content: groovy.util.Node.() -> Unit) {
        appendNode(key).also(content)
      }

      asNode().run {
        add("name", "better-parse")
        add(
            "description",
            "A library that provides a set of parser combinator tools for building parsers and translators in Kotlin.")
        add("url", "https://github.com/h0tk3y/better-parse")
        node("organization") {
          add("name", "com.github.h0tk3y")
          add("url", "https://github.com/h0tk3y")
        }
        node("issueManagement") {
          add("system", "github")
          add("url", "https://github.com/h0tk3y/better-parse/issues")
        }
        node("licenses") {
          node("license") {
            add("name", "Apache License 2.0")
            add("url", "https://raw.githubusercontent.com/h0tk3y/better-parse/master/LICENSE")
            add("distribution", "repo")
          }
        }
        node("scm") {
          add("url", "https://github.com/h0tk3y/better-parse")
          add("connection", "scm:git:git://github.com/h0tk3y/better-parse")
          add("developerConnection", "scm:git:ssh://github.com/h0tk3y/better-parse.git")
        }
        node("developers") { node("developer") { add("name", "h0tk3y") } }
      }
    }

publishing {
  repositories {
    maven {
      name = "GitHubPackages"
      url = uri("https://maven.pkg.github.com/bettermarks/better-parse")

      credentials {
        username = System.getenv("GITHUB_ACTOR")
        password = System.getenv("GITHUB_TOKEN")
      }
    }
  }

  publications.withType<MavenPublication>().all { customizeForMavenCentral(pom) }
}

// endregion
