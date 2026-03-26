plugins {
    id(libs.plugins.kotlin.js.get().pluginId)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(rootProject)
}

kotlin {
    js(IR) {
        nodejs()
        binaries.executable()
    }
}

var assembleWeb = tasks.register<Sync>("assembleWeb") {
    val main by kotlin.js().compilations.getting

    from(project.provider {
        main.compileDependencyFiles.map { it.absolutePath }.map(::zipTree).map {
            it.matching {
                include("*.js")
                exclude("**/META-INFΩ/**")
            }
        }
    })

    from(main.compileTaskProvider.map { it.destinationDirectory })
    from(kotlin.sourceSets.main.get().resources) { include("*.html") }
    into("${layout.buildDirectory.get()}/web")
}

tasks.assemble {
    dependsOn(assembleWeb)
}