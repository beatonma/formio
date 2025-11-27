package org.beatonma.gclocks.core.glyph

import org.beatonma.gclocks.core.options.GlyphOptions
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.core.util.getCurrentTimeMillis
import org.beatonma.gclocks.core.util.progress

private typealias OnVisibilityChanged = ((GlyphVisibility, currentTimeMillis: Long) -> Unit)


sealed interface GlyphVisibilityController : SecondChangedObserver {
    val visibility: GlyphVisibility
    val onVisibilityChanged: OnVisibilityChanged?

    fun setVisibility(newVisibility: GlyphVisibility, currentTimeMillis: Long)

    fun setState(newVisibility: GlyphVisibility, force: Boolean, currentTimeMillis: Long)

    fun tick(options: GlyphOptions, currentTimeMillis: Long): GlyphVisibility?

    fun appear(currentTimeMillis: Long)
    fun disappear(currentTimeMillis: Long)
}

abstract class BaseGlyphVisibilityController(
    override val onVisibilityChanged: OnVisibilityChanged? = null,
) : GlyphVisibilityController {
    override var visibility: GlyphVisibility = GlyphVisibility.Appearing
        protected set

    override fun setVisibility(
        newVisibility: GlyphVisibility,
        currentTimeMillis: Long
    ) {
        val isChanged = visibility != newVisibility
        visibility = newVisibility
        if (isChanged) {
            onVisibilityChanged?.invoke(newVisibility, currentTimeMillis)
        }
    }
}


class SynchronizedVisibilityController(
    onVisibilityChanged: OnVisibilityChanged? = null,
) : BaseGlyphVisibilityController(onVisibilityChanged) {
    private var pendingVisibility: GlyphVisibility? = null

    override fun onSecondChange(currentTimeMillis: Long) {
        val pending = pendingVisibility

        if (pending != null) {
            applyVisibility(pending)
        } else {
            if (visibility == GlyphVisibility.Appearing) {
                applyVisibility(GlyphVisibility.Visible)
            } else if (visibility == GlyphVisibility.Disappearing) {
                applyVisibility(GlyphVisibility.Hidden)
            }
        }
    }

    override fun tick(
        options: GlyphOptions,
        currentTimeMillis: Long
    ): GlyphVisibility? {
        // no-op - time changes handled by onSecondChange()
        return null
    }

    override fun setState(
        newVisibility: GlyphVisibility,
        force: Boolean,
        currentTimeMillis: Long
    ) {
        if (force) {
            applyVisibility(newVisibility)
            return
        }

        when (newVisibility) {
            GlyphVisibility.Visible, GlyphVisibility.Appearing -> appear(currentTimeMillis)
            GlyphVisibility.Disappearing, GlyphVisibility.Hidden -> disappear(currentTimeMillis)
        }
    }

    override fun appear(currentTimeMillis: Long) {
        when (visibility) {
            GlyphVisibility.Visible -> cancelPending()
            GlyphVisibility.Hidden, GlyphVisibility.Disappearing -> {
                queuePending(GlyphVisibility.Appearing)
            }

            GlyphVisibility.Appearing -> {
                queuePending(GlyphVisibility.Visible)
            }
        }
    }

    override fun disappear(currentTimeMillis: Long) {
        when (visibility) {
            GlyphVisibility.Hidden -> cancelPending()
            GlyphVisibility.Visible, GlyphVisibility.Appearing -> {
                queuePending(GlyphVisibility.Disappearing)
            }

            GlyphVisibility.Disappearing -> {
                queuePending(GlyphVisibility.Hidden)
            }

            else -> debug(false) {
                debug("disappear() has no effect when visibility == $visibility")
            }
        }
    }

    private fun applyVisibility(newVisibility: GlyphVisibility) {
        cancelPending()
        visibility = newVisibility
    }

    private fun queuePending(newVisibility: GlyphVisibility) {
        pendingVisibility = newVisibility
    }

    private fun cancelPending() {
        pendingVisibility = null
    }
}


class DesynchronizedGlyphVisibilityController(
    currentTimeMillis: Long = getCurrentTimeMillis(),
    onVisibilityChanged: OnVisibilityChanged? = null,
) : BaseGlyphVisibilityController(onVisibilityChanged) {
    var visibilityChangedAt: Long = currentTimeMillis
    var visibilityChangedProgress: Float = 0f

    override fun setState(
        newVisibility: GlyphVisibility,
        force: Boolean,
        currentTimeMillis: Long
    ) {
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

    override fun onSecondChange(currentTimeMillis: Long) {
        // no-op - time changes handled by tick()
    }

    override fun tick(options: GlyphOptions, currentTimeMillis: Long): GlyphVisibility? {
        val millisSinceVisibilityChange = currentTimeMillis - visibilityChangedAt
        val visibilityChangeDurationMillis = options.visibilityChangeDurationMillis
        visibilityChangedProgress =
            progress(
                millisSinceVisibilityChange.toFloat(),
                0f,
                visibilityChangeDurationMillis.toFloat()
            )

        val isVisibilityTransitionExpired: Boolean =
            millisSinceVisibilityChange > visibilityChangeDurationMillis

        if (isVisibilityTransitionExpired) {
            if (visibility == GlyphVisibility.Appearing) return GlyphVisibility.Visible
            if (visibility == GlyphVisibility.Disappearing) return GlyphVisibility.Hidden
        }
        return null
    }

    override fun appear(currentTimeMillis: Long) {
        when (visibility) {
            GlyphVisibility.Hidden, GlyphVisibility.Disappearing -> {
                setState(
                    GlyphVisibility.Appearing,
                    force = true,
                    currentTimeMillis = currentTimeMillis
                )
            }

            else -> debug(false) {
                debug("appear() has no effect when visibility == $visibility")
            }
        }
    }

    override fun disappear(currentTimeMillis: Long) {
        when (visibility) {
            GlyphVisibility.Visible, GlyphVisibility.Appearing -> {
                setState(
                    GlyphVisibility.Disappearing,
                    force = true,
                    currentTimeMillis = currentTimeMillis
                )
            }

            else -> debug(false) {
                debug("disappear() has no effect when visibility == $visibility")
            }
        }
    }
}
