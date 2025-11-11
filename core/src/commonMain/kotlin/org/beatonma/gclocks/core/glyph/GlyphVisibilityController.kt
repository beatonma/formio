package org.beatonma.gclocks.core.glyph

import org.beatonma.gclocks.core.options.GlyphOptions
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.core.util.getCurrentTimeMillis
import org.beatonma.gclocks.core.util.progress


sealed interface GlyphVisibilityController : SecondChangedObserver {
    val visibility: GlyphVisibility
    val onVisibilityChanged: ((GlyphVisibility) -> Unit)?

    fun setState(newVisibility: GlyphVisibility, force: Boolean, currentTimeMillis: Long)

    fun tick(options: GlyphOptions, currentTimeMillis: Long): GlyphVisibility?

    fun appear(currentTimeMillis: Long) {
        when (visibility) {
            GlyphVisibility.Hidden, GlyphVisibility.Disappearing -> {
                setState(GlyphVisibility.Appearing, force = true, currentTimeMillis = currentTimeMillis)
            }

            else -> debug(false) {
                debug("appear() has no effect when visibility == $visibility")
            }
        }
    }

    fun disappear(currentTimeMillis: Long) {
        when (visibility) {
            GlyphVisibility.Visible, GlyphVisibility.Appearing -> {
                setState(GlyphVisibility.Disappearing, force = true, currentTimeMillis = currentTimeMillis)
            }

            else -> debug(false) {
                debug("disappear() has no effect when visibility == $visibility")
            }
        }
    }
}

abstract class BaseGlyphVisibilityController(
    override val onVisibilityChanged: ((GlyphVisibility) -> Unit)? = null,
) : GlyphVisibilityController {
    override var visibility: GlyphVisibility = GlyphVisibility.Appearing
        protected set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) {
                onVisibilityChanged?.invoke(value)
            }
        }
}


class SynchronizedVisibilityController(
    onVisibilityChanged: ((GlyphVisibility) -> Unit)? = null,
) : BaseGlyphVisibilityController(onVisibilityChanged) {
    private var pendingVisibility: GlyphVisibility? = null

    override fun onSecondChange(currentTimeMillis: Long) {
        val pending = pendingVisibility

        if (pending != null) {
            visibility = pending
            pendingVisibility = null
        } else {
            if (visibility == GlyphVisibility.Appearing) {
                visibility = GlyphVisibility.Visible
            } else if (visibility == GlyphVisibility.Disappearing) {
                visibility = GlyphVisibility.Hidden
            }
        }
    }

    override fun setState(
        newVisibility: GlyphVisibility,
        force: Boolean,
        currentTimeMillis: Long
    ) {
        if (force) {
            // Defer visibility change until the start of the next second
            pendingVisibility = newVisibility
            return
        }

        when (newVisibility) {
            GlyphVisibility.Visible, GlyphVisibility.Appearing -> appear(currentTimeMillis)
            GlyphVisibility.Disappearing, GlyphVisibility.Hidden -> disappear(currentTimeMillis)
        }
    }

    override fun tick(
        options: GlyphOptions,
        currentTimeMillis: Long
    ): GlyphVisibility? {
        return null
    }
}


class DesynchronizedGlyphVisibilityController(
    currentTimeMillis: Long = getCurrentTimeMillis(),
    onVisibilityChanged: ((GlyphVisibility) -> Unit)? = null,
) : BaseGlyphVisibilityController(onVisibilityChanged) {
    var visibilityChangedAt: Long = currentTimeMillis
    var visibilityChangedProgress: Float = 0f

    override fun setState(newVisibility: GlyphVisibility, force: Boolean, currentTimeMillis: Long) {
        if (force) {
            if (newVisibility != visibility) {
                visibilityChangedAt = currentTimeMillis
                visibilityChangedProgress = 0f
            }
            visibility = newVisibility
            return
        }

        when (newVisibility) {
            GlyphVisibility.Visible, GlyphVisibility.Appearing -> appear(currentTimeMillis)
            GlyphVisibility.Disappearing, GlyphVisibility.Hidden -> disappear(currentTimeMillis)
        }
    }

    override fun tick(options: GlyphOptions, currentTimeMillis: Long): GlyphVisibility? {
        val millisSinceVisibilityChange = currentTimeMillis - visibilityChangedAt
        val visibilityChangeDurationMillis = options.visibilityChangeDurationMillis
        visibilityChangedProgress =
            progress(millisSinceVisibilityChange.toFloat(), 0f, visibilityChangeDurationMillis.toFloat())

        val isVisibilityTransitionExpired: Boolean = millisSinceVisibilityChange > visibilityChangeDurationMillis

        if (isVisibilityTransitionExpired) {
            if (visibility == GlyphVisibility.Appearing) return GlyphVisibility.Visible
            if (visibility == GlyphVisibility.Disappearing) return GlyphVisibility.Hidden
        }
        return null
    }

    override fun onSecondChange(currentTimeMillis: Long) {
        // no-op
    }
}
