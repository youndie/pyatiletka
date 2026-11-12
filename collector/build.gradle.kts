// GitHub -> Snapshot: a library for the desktop app and, later, a CLI for the hourly workflow
// (docs/services/collector.md). Empty until B-07; here so the module graph is the one the documents describe.
plugins {
    alias(wip.plugins.kotlinJvm)
    id("io.github.youndie.sborka.jvm")
    id("io.github.youndie.sborka.lint")
}

dependencies {
    implementation(project(":core"))
}
