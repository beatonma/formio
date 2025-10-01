import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val wasmOutputDirectoryPath: String = "${layout.buildDirectory.get()}/outputs/wasmJs"

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.get().compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName.set("composeApp")
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = when (mode) {
                    KotlinWebpackConfig.Mode.PRODUCTION -> {
                        val timestamp: String =
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                        "gclocks-${timestamp}.js"
                    }

                    KotlinWebpackConfig.Mode.DEVELOPMENT -> "gclocks.js"
                }

                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }

            @OptIn(ExperimentalDistributionDsl::class)
            distribution {
                outputDirectory = File(wasmOutputDirectoryPath)
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
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.material3AdaptiveNavigationSuite)
            implementation(compose.materialIconsExtended)
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.navigation)
            implementation(libs.compose.material3Adaptive)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ui.backhandler)
            implementation(project(":core"))
            implementation(project(":clocks:form"))
            implementation(project(":clocks:io16"))
            implementation(project(":clocks:io18"))
        }
        jvmMain.dependencies {
            implementation(libs.androidx.datastore.preferences)
        }
        androidMain.dependencies {
            implementation(compose.preview)
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
    }
}

android {
    namespace = "org.beatonma"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.beatonma.gclocks"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "org.beatonma.gclocks.app.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.beatonma.gclocks"
            packageVersion = "1.0.0"
        }
    }
}


val wasmJsDistributionZip by tasks.registering(Zip::class) {
    description = "Collect the output of wasmJsBrowserDistribution into a zip file"
    group = "distribution"

    dependsOn("wasmJsBrowserDistribution")

    destinationDirectory.set(layout.buildDirectory.dir("outputs"))
    archiveFileName.set("wasmJs.zip")
    from(wasmOutputDirectoryPath) {
        exclude("*.html")
        exclude("*.css")
        into("")
    }
}
