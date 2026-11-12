// The model, the snapshot schema and the board's rules (docs/services/core.md). No Compose, no network.
plugins {
    alias(wip.plugins.kotlinMultiplatform)
    alias(wip.plugins.kotlinSerialization)
    id("io.github.youndie.sborka.kmp")
    id("io.github.youndie.sborka.lint")
}

kotlin {
    jvm("desktop")

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            api(wip.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
