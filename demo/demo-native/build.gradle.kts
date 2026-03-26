import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}
repositories {
    mavenCentral()
}

kotlin {
    linuxX64 {
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(rootProject)
            }
        }

    }
}
