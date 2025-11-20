package org.beatonma.gclocks.core.graphics.paths

import org.beatonma.gclocks.core.graphics.Path
import kotlin.math.min

typealias StyleId = Int
typealias PathTransformation = PathDefinition.Builder.() -> Unit

data class StyledPath(
    val path: PathDefinition,
    val styleId: StyleId,
)

data class PathImage(
    val width: Float,
    val height: Float,
    private val paths: List<StyledPath>,
) {
    fun plot(path: Path, render: (styleId: StyleId) -> Unit) {
        for (styledPath in paths) {
            styledPath.path.plot(path)
            render(styledPath.styleId)
        }
    }

    fun plotInterpolated(
        path: Path,
        other: PathImage,
        progress: Float,
        render: (styleId: StyleId) -> Unit?
    ) {
        paths.forEachIndexed { index, styledPath ->
            styledPath.path.plotInterpolated(path, other.paths[index].path, progress)
            render(styledPath.styleId)
        }
    }

    fun canInterpolateWith(other: PathImage): Boolean {
        if (paths.size != other.paths.size) return false
        for (index in paths.indices) {
            val me = paths[index].path
            val them = other.paths[index].path
            if (!me.canInterpolateWith(them)) return false
        }
        return true
    }

    class Builder(val width: Float, val height: Float) {
        val paths = mutableListOf<StyledPath>()

        // Common keyline positions
        inline val left get() = 0f
        inline val top get() = 0f
        inline val right get() = width
        inline val bottom get() = height
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(centerX, centerY)
        val quarterX = width / 4f
        val quarterY = height / 4f
        val threeQuarterX = width * .75f
        val threeQuarterY = height * .75f
        val thirdX = width / 3f
        val thirdY = height / 3f
        val twoThirdX = thirdX * 2f
        val twoThirdY = thirdY * 2f

        inline fun path(
            styleId: StyleId,
            transformation: PathTransformation = {},
            block: PathDefinition.Builder.() -> Unit
        ) {
            paths.add(
                StyledPath(
                    PathDefinition {
                        block()
                        transformation()
                    },
                    styleId
                )
            )
        }

        fun build(): PathImage = PathImage(width, height, paths)
    }
}


inline fun PathImage(width: Float, height: Float, block: PathImage.Builder.() -> Unit): PathImage =
    PathImage.Builder(width, height).apply(block).build()
