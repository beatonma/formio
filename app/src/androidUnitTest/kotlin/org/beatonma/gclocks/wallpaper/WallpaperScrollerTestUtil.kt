package org.beatonma.gclocks.wallpaper

import kotlinx.coroutines.test.runTest
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.test.shouldbe
import kotlin.test.Test

class WallpaperScroller(private val engine: WallpaperEngineDelegate, totalPages: Int = 1) {
    val xOffsetStep = if (totalPages < 2) 0f else 1f / (totalPages - 1)
    val totalPages = totalPages - 1
    var x = 0f
        private set

    private var currentPage = 0
        set(value) {
            field = when {
                value > totalPages -> 0
                value < 0 -> totalPages
                else -> value
            }
            if (totalPages == 0) x = 0f
            else x = (field.toFloat() / totalPages.toFloat())
            debug("scroll to page ${field + 1}")
        }

    init {
        // Call onOffsetsChanged with initial position
        applyScroll()
    }

    fun scrollToNextPage() {
        currentPage += 1
        applyScroll()
    }

    fun scrollToPreviousPage() {
        currentPage -= 1
        applyScroll()
    }

    private fun applyScroll() {
        engine.onOffsetsChanged(
            xOffset = x,
            yOffset = 0f,
            xOffsetStep = xOffsetStep,
            yOffsetStep = 0f,
            xPixelOffset = 0,
            yPixelOffset = 0,
        )
    }
}


class WallpaperScrollerTest {
    @Test
    fun `meta -- WallpaperScroller test-helper class is correct`() = runTest {
        createEngineDelegate(visibleOnLauncherPages = listOf()).let { engine ->
            WallpaperScroller(engine, totalPages = 1).let { scroller ->
                scroller.xOffsetStep shouldbe 0f
                scroller.x shouldbe 0f
                scroller.scrollToNextPage()
                scroller.x shouldbe 0f
                scroller.scrollToPreviousPage()
                scroller.x shouldbe 0f
            }

            WallpaperScroller(engine, totalPages = 2).let { scroller ->
                scroller.xOffsetStep shouldbe 1f
                scroller.x shouldbe 0f
                scroller.scrollToNextPage()
                scroller.x shouldbe 1f
                scroller.scrollToNextPage()
                scroller.x shouldbe 0f

                scroller.scrollToPreviousPage()
                scroller.x shouldbe 1f
                scroller.scrollToPreviousPage()
                scroller.x shouldbe 0f
            }

            WallpaperScroller(engine, totalPages = 3).let { scroller ->
                scroller.xOffsetStep shouldbe 0.5f
                scroller.x shouldbe 0f
                scroller.scrollToNextPage()
                scroller.x shouldbe 0.5f
                scroller.scrollToNextPage()
                scroller.x shouldbe 1f
                scroller.scrollToNextPage()
                scroller.x shouldbe 0f

                scroller.scrollToPreviousPage()
                scroller.x shouldbe 1f
                scroller.scrollToPreviousPage()
                scroller.x shouldbe 0.5f
                scroller.scrollToPreviousPage()
                scroller.x shouldbe 0f
            }

            WallpaperScroller(engine, totalPages = 4).let { scroller ->
                scroller.xOffsetStep shouldbe 1f / 3f
                scroller.x shouldbe 0f
                scroller.scrollToNextPage()
                scroller.x shouldbe 1f / 3f
                scroller.scrollToNextPage()
                scroller.x shouldbe 2f / 3f
                scroller.scrollToNextPage()
                scroller.x shouldbe 1f

                scroller.scrollToPreviousPage()
                scroller.x shouldbe 2f / 3f
                scroller.scrollToPreviousPage()
                scroller.x shouldbe 1f / 3f
                scroller.scrollToPreviousPage()
                scroller.x shouldbe 0f
            }
        }
    }
}
