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
