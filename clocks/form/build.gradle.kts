import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvm {}

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {}
    }

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(project(":test"))
        }
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(project(":core"))
        }
    }
}
