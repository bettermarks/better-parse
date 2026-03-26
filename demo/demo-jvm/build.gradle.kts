plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(rootProject)
    implementation(kotlin("stdlib"))
}