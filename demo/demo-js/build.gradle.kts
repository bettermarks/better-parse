plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

repositories {
    mavenCentral()
}

kotlin {
    sourceSets {
        jsMain {
            dependencies {
                implementation(rootProject)
            }
        }
    }
    js(IR) {
        browser {
            distribution {
                outputDirectory.set(layout.buildDirectory.dir("web"))
            }
        }
        binaries.executable()
    }
}
