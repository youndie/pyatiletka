// Every screen on plakat-ui (docs/services/skin-plakat.md). Depends on core and on its kit, and on nothing
// of the other skin (research D3).
plugins {
    alias(wip.plugins.kotlinMultiplatform)
    alias(wip.plugins.composeMultiplatform)
    alias(wip.plugins.composeCompiler)
    id("io.github.youndie.sborka.kmp")
    id("io.github.youndie.sborka.lint")
}

kotlin {
    jvm("desktop")

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        // Required by the Compose plugin's own check (CMP-4906): without an executable binary the Skiko
        // runtime cannot be bundled for the target's tests (both kits declare the same).
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
            implementation(libs.plakat.core)
            implementation(wip.compose.runtime)
            implementation(wip.compose.foundation)
            implementation(wip.compose.ui)
        }
    }
}
