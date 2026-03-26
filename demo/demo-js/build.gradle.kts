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
    }
}

var assembleWeb = task<Sync>("assembleWeb") {
    val main by kotlin.js().compilations.getting

    from(project.provider {
        main.compileDependencyFiles.map { it.absolutePath }.map(::zipTree).map {
            it.matching {
                include("*.js")
                exclude("**/META-INFΩ/**")
            }
        }
    })

    from(main.compileKotlinTaskProvider.map { it.destinationDirectory })
    from(kotlin.sourceSets.main.get().resources) { include("*.html") }
    into("${buildDir}/web")
}

tasks.assemble {
    dependsOn(assembleWeb)
}