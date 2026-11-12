// The shell: entry points for the desktop window and the web page, and later the snapshot sources,
// screen state and navigation (docs/services/app.md).
plugins {
    alias(wip.plugins.kotlinMultiplatform)
    alias(wip.plugins.composeMultiplatform)
    alias(wip.plugins.composeCompiler)
    alias(wip.plugins.kotlinSerialization)
    id("io.github.youndie.sborka.kmp")
    id("io.github.youndie.sborka.lint")
}

kotlin {
    jvm("desktop")

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser { commonWebpackConfig { outputFileName = "pyatiletka.js" } }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
            implementation(wip.kotlinx.coroutines.core)
            implementation(project(":skin-plakat"))
            implementation(project(":skin-toon"))
            implementation(wip.compose.runtime)
            implementation(wip.compose.foundation)
            implementation(wip.compose.ui)
        }
        getByName("desktopMain").dependencies {
            implementation(project(":collector"))
            implementation(compose.desktop.currentOs)
            implementation(libs.appframe)
            implementation("io.ktor:ktor-client-cio:${wip.versions.ktor.get()}")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(wip.kotlinx.coroutines.test)
        }
        getByName("desktopTest").dependencies {
            implementation(kotlin("test"))
            implementation(wip.compose.ui.test)
            implementation("io.ktor:ktor-client-mock:${wip.versions.ktor.get()}")
        }
    }
}

compose.desktop.application {
    mainClass = "io.github.youndie.pyatiletka.app.MainKt"
    nativeDistributions { packageName = "pyatiletka" }
}
