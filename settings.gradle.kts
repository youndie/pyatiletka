pluginManagement {
    repositories {
        // AGP is published only here; filtered, so an unreachable host fails nothing it does not hold.
        google {
            content {
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
                includeGroupAndSubgroups("androidx")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        // The build conventions are not on the plugin portal. Spelled out by hand because
        // `pluginManagement` is evaluated before any settings plugin applies.
        maven("https://reposilite.kotlin.website/snapshots") {
            content { includeGroupAndSubgroups("io.github.youndie") }
        }
    }
}

plugins {
    // Repositories with their filters (the snapshot server for `io.github.youndie.*`, where both kits
    // live until Maven Central), and the `wip` catalog: the compiler, AGP and Compose Multiplatform
    // come from it. The same sborka the kits are built with (research §1.2).
    id("io.github.youndie.sborka.settings") version "0.5.0.135"
}

rootProject.name = "pyatiletka"

include(":core")
include(":collector")
include(":app")
include(":skin-plakat")
include(":skin-toon")
