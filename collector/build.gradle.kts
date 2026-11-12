// GitHub -> Snapshot (docs/services/collector.md): a library the desktop app calls, and a CLI the hourly
// workflow runs (`./gradlew :collector:installDist`, then `collector/build/install/collector/bin/collector`).
plugins {
    alias(wip.plugins.kotlinJvm)
    alias(wip.plugins.kotlinSerialization)
    application
    id("io.github.youndie.sborka.jvm")
    id("io.github.youndie.sborka.lint")
}

val ktor = wip.versions.ktor.get()

dependencies {
    api(project(":core"))
    implementation("io.ktor:ktor-client-core:$ktor")
    implementation("io.ktor:ktor-client-cio:$ktor")
    implementation(wip.kotlinx.coroutines.core)
    testImplementation("io.ktor:ktor-client-mock:$ktor")
    testImplementation(wip.kotlinx.coroutines.test)
}

application {
    mainClass = "io.github.youndie.pyatiletka.collector.MainKt"
    applicationName = "collector"
}
