package buildlogic

import org.gradle.api.artifacts.VersionCatalog
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

open class BuildTargetsKmpConvention : KmpConvention() {
    override fun configureKotlin(kotlin: KotlinMultiplatformExtension, libs: VersionCatalog) {
        kotlin.apply {
            jvm()

            @OptIn(ExperimentalWasmDsl::class)
            wasmJs {
                browser()
            }
        }
    }
}