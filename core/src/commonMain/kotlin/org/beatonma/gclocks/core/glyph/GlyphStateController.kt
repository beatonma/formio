package org.beatonma.gclocks.core.glyph

import org.beatonma.gclocks.core.options.GlyphOptions
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.core.util.progress

sealed interface GlyphStateController {
    val state: GlyphState
    val lock: GlyphState?
    val stateChangeProgress: Float

    fun setState(newState: GlyphState, force: Boolean, currentTimeMillis: Long)
    fun tick(options: GlyphOptions, currentTimeMillis: Long): GlyphState?
}

class DefaultGlyphStateController(lock: GlyphState?, currentTimeMillis: Long) : GlyphStateController {
    override var state: GlyphState = lock ?: GlyphState.Active
        private set
    override val lock: GlyphState? = when (lock) {
        null -> null
        GlyphState.Activating, GlyphState.Active -> GlyphState.Active
        GlyphState.Deactivating, GlyphState.Inactive -> GlyphState.Inactive
    }

    var stateChangedAt: Long = currentTimeMillis
    override var stateChangeProgress: Float = 0f

    override fun setState(newState: GlyphState, force: Boolean, currentTimeMillis: Long) {
        if (lock != null && !state.isTransitional) {
            // Lock is overruled when current state is transitional and can tick over to a steady state
            return
        }

        if (force) {
            if (newState == GlyphState.Active || newState != state) {
                stateChangedAt = currentTimeMillis
                stateChangeProgress = 0f
            }
            state = newState
            return
        }

        when (newState) {
            GlyphState.Activating,
            GlyphState.Active,
                -> setActive(currentTimeMillis)

            GlyphState.Deactivating,
            GlyphState.Inactive,
                -> setInactive(currentTimeMillis)
        }
    }

    override fun tick(options: GlyphOptions, currentTimeMillis: Long): GlyphState? {
        val millisSinceStateChange = currentTimeMillis - stateChangedAt

        stateChangeProgress =
            progress(millisSinceStateChange.toFloat(), 0f, options.stateChangeDurationMillis.toFloat())

        if (state == GlyphState.Active && millisSinceStateChange > options.activeStateDurationMillis) {
            // Active state decays after period of inactivity
            return GlyphState.Deactivating
        }

        if (millisSinceStateChange > options.stateChangeDurationMillis) {
            if (state == GlyphState.Activating) return GlyphState.Active
            if (state == GlyphState.Deactivating) return GlyphState.Inactive
        }
        return null
    }

    private fun setActive(currentTimeMillis: Long) {
        when (state) {
            GlyphState.Active -> setState(GlyphState.Active, force = true, currentTimeMillis = currentTimeMillis)

            GlyphState.Inactive, GlyphState.Deactivating -> setState(
                GlyphState.Activating,
                force = true,
                currentTimeMillis = currentTimeMillis
            )

            GlyphState.Activating -> debug(false) {
                debug("setActive has no effect when state == $state")
            }
        }
    }

    private fun setInactive(currentTimeMillis: Long) {
        when (state) {
            GlyphState.Active, GlyphState.Activating -> setState(
                GlyphState.Deactivating,
                force = true,
                currentTimeMillis = currentTimeMillis
            )

            else -> debug(false) {
                debug("setInactive has no effect when state == $state")
            }
        }
    }
}
