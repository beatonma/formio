package org.beatonma.gclocks.core.graphics.paths

import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.core.util.progressIn


fun interface PathAnimation {
    fun plot(path: Path, progress: Float, render: (styleId: StyleId) -> Unit)
}

class MultipartAnimation(
    private val animations: List<PathAnimation>,
) : PathAnimation {
    override fun plot(
        path: Path,
        progress: Float,
        render: (styleId: StyleId) -> Unit
    ) {
        for (anim in animations) {
            anim.plot(path, progress, render)
        }
    }
}


class KeyframeAnimation(
    val canonical: PathImage,
    val easing: (Float) -> Float = { it },
    val transitions: List<KeyframeTransition>,

    /** Set true if only one [KeyframeTransition] is running at any given moment.
     * i.e. [transitions] does not contain any overlapping start/end periods. */
    val isLinear: Boolean = false,
) : PathAnimation {
    override fun plot(path: Path, progress: Float, render: (styleId: StyleId) -> Unit) {
        if (progress == 0f) return canonical.plot(path, render)

        for (transition in transitions) {
            val (start, end) = transition

            if (progress in start.progress..<end.progress) {
                start.image.plotInterpolated(
                    path,
                    end.image,
                    easing(progress.progressIn(start.progress, end.progress)),
                    render
                )

                if (isLinear) {
                    // When isLinear, only one transition can run at a time.
                    break
                }
            }
        }
    }
}

class StaticImage(
    val image: PathImage,
    val start: Float = 0f,
    val end: Float = 1f,
) : PathAnimation {
    override fun plot(
        path: Path,
        progress: Float,
        render: (styleId: StyleId) -> Unit
    ) {
        if (progress in start..<end) {
            image.plot(path, render)
        }
    }
}

data class Keyframe(
    val progress: Float,
    val image: PathImage,
)

data class KeyframeTransition(
    val start: Keyframe,
    val end: Keyframe,
) {
    init {
        debug {
            require(end.progress > start.progress) {
                "KeyframeTransition has zero duration"
            }
            require(start.image.canInterpolateWith(end.image)) {
                "KeyframeTransition received start and end states that cannot be interpolated"
            }
        }
    }
}
