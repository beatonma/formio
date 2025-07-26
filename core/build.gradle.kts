plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm {

    }
    sourceSets.commonTest.dependencies {
        implementation(kotlin("test"))
    }
}
