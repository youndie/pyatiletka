// Every screen on toon-ui (docs/services/skin-toon.md). Depends on core and on its kit, and on nothing
// of the other skin (research D3).
plugins {
    alias(wip.plugins.kotlinMultiplatform)
    alias(wip.plugins.composeMultiplatform)
    alias(wip.plugins.composeCompiler)
    alias(wip.plugins.ksp)
    alias(libs.plugins.viddik)
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
            implementation(libs.toon.core)
            implementation(wip.compose.runtime)
            implementation(wip.compose.foundation)
            implementation(wip.compose.ui)
        }
        getByName("desktopTest").dependencies {
            implementation(kotlin("test"))
            implementation(compose.desktop.currentOs)
            implementation(wip.compose.ui.test)
        }
    }
}

// Goldens verified by `check`, the kit's own setup (research §1.2). The cartoon kit has no canvas
// (research D6), so there is no design parity here: the goldens are the record, recorded on Linux,
// where CI verifies them (B-14).
viddik {
    verifyOnCheck.set(true)
    shards.set(1)
    designTolerancePercent.set(5.0)
    designChannelTolerance.set(16)
    designStrict.set(false)
}

tasks.withType<Test>().configureEach {
    inputs
        .dir(layout.projectDirectory.dir("src/desktopTest/snapshots"))
        .withPropertyName("viddikSnapshots")
        .withPathSensitivity(org.gradle.api.tasks.PathSensitivity.RELATIVE)
}
