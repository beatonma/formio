package buildlogic

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


class ProjectGlobals : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.create<ProjectGlobalsExtension>("projectGlobals")
        }
    }
}

abstract class ProjectGlobalsExtension @Inject constructor(project: Project) {
    val projectName: String = "formio"
    val projectNameUI: String = "FormIO"
    val projectId: String = "org.beatonma.formio"

    val javaVersion: JavaVersion = JavaVersion.VERSION_21

    val projectVersionName: String = "1.0.0"
    val projectVersionCode: Int = Git.commitCount(project)

    val timestamp: String = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))

    val androidMinSdk = 30
    val androidTargetSdk = 36
    val androidCompileSdk = 36

    fun projectPackage(path: String): String = "${projectId}.${path.removePrefix(projectId).removePrefix(".")}"
    fun filename(fileType: String, suffix: String? = null): String {
        return listOfNotNull(
            projectName,
            timestamp,
            suffix,
            ".${fileType.removePrefix(".")}"
        ).joinToString("-")
    }
}