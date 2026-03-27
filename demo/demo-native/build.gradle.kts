plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.ktfmt)
}

repositories { mavenCentral() }

kotlin {
  linuxX64 { binaries.executable() }
  linuxArm64() { binaries.executable() }

  sourceSets {
    val commonMain by getting { dependencies { api(rootProject) } }

  }
}
