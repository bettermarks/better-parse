import org.apache.tools.ant.taskdefs.condition.Os
import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeTargetPreset
import java.net.URI

plugins {
    alias(libs.plugins.kotlin.multiplatform)

    id("maven-publish")
}

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    sourceSets {
        all {
            languageSettings.optIn("kotlin.ExperimentalMultiplatform")
        }

        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }

    jvm {
        compilations["test"].defaultSourceSet.dependencies {
            implementation(kotlin("test-junit"))
        }
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    js(IR) {
//        browser()
        nodejs()

        compilations["test"].defaultSourceSet.dependencies {
            implementation(kotlin("test-js"))
        }
        compilations.all {
            kotlinOptions.moduleKind = "umd"
        }
    }

    linuxX64()
}

//region Code generation

val codegen by tasks.registering {
    val maxTupleSize = 16

    andCodegen(
        maxTupleSize,
        kotlin.sourceSets.commonMain.get().kotlin.srcDirs.first().absolutePath + "/generated/andFunctions.kt"
    )
    tupleCodegen(
        maxTupleSize,
        kotlin.sourceSets.commonMain.get().kotlin.srcDirs.first().absolutePath + "/generated/tuples.kt"
    )
}

kotlin.sourceSets.commonMain {
    kotlin.srcDirs(files().builtBy(codegen))
}

//endregion


fun customizeForMavenCentral(pom: org.gradle.api.publish.maven.MavenPom) = pom.withXml {
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
            "A library that provides a set of parser combinator tools for building parsers and translators in Kotlin."
        )
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
        node("developers") {
            node("developer") {
                add("name", "h0tk3y")
            }
        }
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

    publications.withType<MavenPublication>().all {
        customizeForMavenCentral(pom)
    }
}

//endregion