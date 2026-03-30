import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.kotlin.allopen)
  alias(libs.plugins.kotlinx.benchmark)
  alias(libs.plugins.ktfmt)
}

repositories { mavenCentral() }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(rootProject)
        implementation(libs.kotlinx.benchmark.runtime)
        implementation(libs.kotlinx.serialization.json)
      }
    }
    commonTest { dependencies { implementation(kotlin("test")) } }
  }

  jvm { compilerOptions { jvmTarget = JvmTarget.JVM_21 } }

  js(IR) { nodejs() }

  linuxX64 {}
}

allOpen.annotation("org.openjdk.jmh.annotations.State")

benchmark {
  targets.register("jvm")
  targets.register("js")

  //  TODO: enable Kotlin/Native benchmark once the issue
  //   with kotlinx.benchmarks 0.3.1 and Kotlin 1.5.21+ compatibility is resolved
  //    targets.register("macosX64")
  //    targets.register("linuxX64")
  //    targets.register("mingwX64")

  configurations["main"].apply {
    warmups = 5
    iterations = 10
  }
}
