import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

dependencies {
    compileOnly(libs.gradle.plugin.android)
    compileOnly(libs.gradle.plugin.compose)
    compileOnly(libs.gradle.plugin.kotlin)
    implementation(files(layout.buildDirectory.files("generated-sources/kotlin/gradle-kotlin-dsl-accessors")))
}


gradlePlugin {
    plugins {
        register("ClockKmpConvention") {
            id = "conventions.kmp.clock"
            implementationClass = "buildlogic.ClockKmpConvention"
        }
        register("BuildTargetsKmpConvention") {
            id = "conventions.kmp.build-targets"
            implementationClass = "buildlogic.BuildTargetsKmpConvention"
        }
        register("KmpConvention") {
            id = "conventions.kmp"
            implementationClass = "buildlogic.KmpConvention"
        }
        register("ProjectGlobals") {
            id = "extensions.project-globals"
            implementationClass = "buildlogic.ProjectGlobals"
        }
    }
}