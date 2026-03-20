package buildlogic

import org.gradle.api.artifacts.VersionCatalog
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension


class ClockKmpConvention : BuildTargetsKmpConvention() {
    override fun configureKotlin(kotlin: KotlinMultiplatformExtension, libs: VersionCatalog) {
        super.configureKotlin(kotlin, libs)
        kotlin.apply {
            sourceSets {
                commonMain.dependencies {
                    implementation(project(":core"))
                }
            }
        }
    }
}