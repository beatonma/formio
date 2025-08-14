plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm {}

    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.annotation)
            implementation(project(":core"))
        }
    }
}