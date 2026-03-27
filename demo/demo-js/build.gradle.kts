@file:OptIn(ExperimentalDistributionDsl::class)

import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.ktfmt)
}

repositories { mavenCentral() }

kotlin {
  sourceSets { jsMain { dependencies { implementation(rootProject) } } }
  js(IR) {
    browser { distribution { outputDirectory.set(layout.buildDirectory.dir("web")) } }
    binaries.executable()
  }
}
