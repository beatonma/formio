package buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

open class KmpConvention : Plugin<Project> {
    open val plugins: Array<out String> = arrayOf(
        "kotlin-multiplatform",
        "kotlin-serialization",
    )

    override fun apply(target: Project) {
        val libs = target.libs
        with(target) {
            applyPlugins(libs, *this@KmpConvention.plugins)

            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets {
                    commonMain.dependencies {
                        implementation(libs.library("kotlinx-serialization-json"))
                    }
                }

                configureKotlin(this, libs)
            }
        }
    }

    private fun Project.applyPlugins(libs: VersionCatalog, vararg pluginAlias: String) {
        for (alias in pluginAlias) {
            pluginManager.apply(libs.plugin(alias))
        }
    }

    open fun configureKotlin(kotlin: KotlinMultiplatformExtension, libs: VersionCatalog) {}
}