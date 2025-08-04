plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

dependencies {
    commonMainImplementation(project(":core"))
}

kotlin {
    jvm {

    }
}