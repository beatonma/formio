import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

val wasmOutputDirectoryPath: String = "${layout.buildDirectory.get()}/outputs/wasmJs"

plugins {
    id("conventions.kmp")
    id("extensions.project-globals")
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.hotReload)
}

kotlin {
    compilerOptions {
        optIn.add("kotlin.time.ExperimentalTime")
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    jvm("desktop")

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(projectGlobals.jvmTarget)
        }
    }


    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "${projectGlobals.projectName}.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting
        val jvmMain by creating { dependsOn(commonMain) }
        val androidMain by getting { dependsOn(jvmMain) }
        val desktopMain by getting { dependsOn(jvmMain) }

        commonMain.dependencies {
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material3Adaptive)
            implementation(libs.compose.material3NavSuite)
            implementation(libs.compose.materialIconsExtended)
            implementation(libs.compose.navigation)
            implementation(libs.compose.resources)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.uiBackhandler)
            implementation(libs.compose.uiToolingPreview)

            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodelCompose)

            implementation(project(":core"))
            implementation(project(":clocks:form"))
            implementation(project(":clocks:io16"))
            implementation(project(":clocks:io18"))
        }
        jvmMain.dependencies {
            implementation(libs.androidx.datastore.preferences)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.lifecycle.service)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.androidx.collection.ktx)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.skiko.awt.runtime.linux.x64)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(project(":test"))
        }

        androidUnitTest.dependencies {
            implementation(libs.kotlinx.coroutinesTest)
        }
    }
}

android {
    namespace = projectGlobals.projectId
    compileSdk = projectGlobals.androidCompileSdk

    defaultConfig {
        applicationId = projectGlobals.projectId
        minSdk = projectGlobals.androidMinSdk
        targetSdk = projectGlobals.androidTargetSdk
        versionCode = projectGlobals.projectVersionCode
        versionName = projectGlobals.projectVersionName

        manifestPlaceholders["app_name"] = projectGlobals.projectNameUI

    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = projectGlobals.javaVersion
        targetCompatibility = projectGlobals.javaVersion
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = projectGlobals.projectPackage("app.MainKt")

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = projectGlobals.projectId
            packageVersion = projectGlobals.projectVersionName
        }
    }
}


val wasmJsDistributionZip by tasks.registering(Zip::class) {
    description = "Collect the output of wasmJsBrowserDistribution into a zip file"
    group = "distribution"

    dependsOn("wasmJsBrowserDistribution")

    destinationDirectory.set(layout.buildDirectory.dir("outputs"))
    archiveFileName.set(projectGlobals.filename("zip", suffix = "wasmJs"))

    from(wasmOutputDirectoryPath) {
        exclude("*.html")
        exclude("*.css")
        into("")
    }
}
