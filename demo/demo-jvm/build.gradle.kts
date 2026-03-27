plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.ktfmt)
}

repositories { mavenCentral() }

dependencies {
  implementation(rootProject)
  implementation(kotlin("stdlib"))
}
