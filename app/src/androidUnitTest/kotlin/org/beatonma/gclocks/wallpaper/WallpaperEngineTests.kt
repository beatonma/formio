@file:OptIn(ExperimentalCoroutinesApi::class)

package org.beatonma.gclocks.wallpaper

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.beatonma.gclocks.android.AndroidCanvasHost
import org.beatonma.gclocks.app.data.settings.DisplayContext
import org.beatonma.gclocks.app.data.settings.DisplayMetrics
import org.beatonma.gclocks.core.geometry.RectF
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.options.AnyOptions
import org.beatonma.gclocks.core.util.getCurrentTimeMillis
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.test.shouldbe
import kotlin.test.Test


class WallpaperEngineTests {
    @Test
    fun `onVisibilityChanged is correct`() = runTest {
        createEngineDelegate().let { engine ->
            // Screen on, keyguard not locked
            engine.shouldBeVisible shouldbe true
            engine.onVisibilityChanged(visible = true, getIsKeyguardLocked = { false })
            engine.shouldBeVisible shouldbe true
        }

        createEngineDelegate().let { engine ->
            // Screen on, keyguard initially locked, then unlocked
            var n = 0
            engine.onVisibilityChanged(visible = true, getIsKeyguardLocked = {
                // simulate KeyguardManager polling: stay locked for 100ms
                n += 10
                n < 100
            })
            engine.shouldBeVisible shouldbe false
            advanceUntilIdle()
            n shouldbe 100
            engine.shouldBeVisible shouldbe true
        }

        createEngineDelegate().let { engine ->
            // Screen off, keyguard not locked
            engine.shouldBeVisible shouldbe true
            engine.onVisibilityChanged(visible = false, getIsKeyguardLocked = { false })
            engine.shouldBeVisible shouldbe false
        }
        createEngineDelegate().let { engine ->
            // Screen off, keyguard locked
            engine.shouldBeVisible shouldbe true
            engine.onVisibilityChanged(visible = false, getIsKeyguardLocked = { true })
            engine.shouldBeVisible shouldbe false
        }
    }

    @Test
    fun `onOffsetsChanged is correct`() = runTest {
        createEngineDelegate(visibleOnLauncherPages = listOf()).let { engine ->
            WallpaperScroller(engine, totalPages = 2).let { scroller ->
                engine.shouldBeVisible shouldbe true
                scroller.scrollToNextPage()
                engine.shouldBeVisible shouldbe true
                scroller.scrollToNextPage()
                engine.shouldBeVisible shouldbe true
            }
        }

        createEngineDelegate(visibleOnLauncherPages = listOf(2)).let { engine ->
            WallpaperScroller(engine, totalPages = 2).let { scroller ->
                engine.shouldBeVisible shouldbe false
                scroller.scrollToNextPage()
                engine.shouldBeVisible shouldbe true
                scroller.scrollToNextPage()
                engine.shouldBeVisible shouldbe false
            }
        }

        createEngineDelegate(visibleOnLauncherPages = listOf(1, 3, 5)).let { engine ->
            WallpaperScroller(engine, totalPages = 5).let { scroller ->
                engine.shouldBeVisible shouldbe true
                scroller.scrollToNextPage()
                engine.shouldBeVisible shouldbe false
                scroller.scrollToNextPage()
                engine.shouldBeVisible shouldbe true
                scroller.scrollToNextPage()
                engine.shouldBeVisible shouldbe false
                scroller.scrollToNextPage()
                engine.shouldBeVisible shouldbe true

                // Back to first page
                scroller.scrollToNextPage()
                engine.shouldBeVisible shouldbe true

                // Back to last page
                scroller.scrollToPreviousPage()
                engine.shouldBeVisible shouldbe true
                scroller.scrollToPreviousPage()
                engine.shouldBeVisible shouldbe false
                scroller.scrollToPreviousPage()
                engine.shouldBeVisible shouldbe true
                scroller.scrollToPreviousPage()
                engine.shouldBeVisible shouldbe false
                scroller.scrollToPreviousPage()
                engine.shouldBeVisible shouldbe true
            }
        }
    }
}


private val TestScope.dispatcher get() = lazy { StandardTestDispatcher(testScheduler) }

internal fun TestScope.createEngineDelegate(
    displayMetrics: DisplayMetrics = DisplayMetrics(refreshRate = 60f),
    clockSettings: AnyOptions = FormOptions(),
    visibleOnLauncherPages: List<Int> = listOf(),
    wallpaperSettings: DisplayContext.Options.Wallpaper = DisplayContext.Options.Wallpaper(
        position = RectF(0f, 0f, 1f, 1f),
        backgroundColor = Color.Red,
        visibleOnLauncherPages = visibleOnLauncherPages,
    ),
    onDraw: (AndroidCanvasHost) -> Unit = {},
    onClearCanvas: (AndroidCanvasHost) -> Unit = {},
    getCurrentTimeMillis: () -> Long = ::getCurrentTimeMillis,
): WallpaperEngineDelegateImpl {
    val engine = WallpaperEngineDelegateImpl(
        isPreview = true,
        isWallpaperVisible = true,
        displayMetrics = flow { emit(displayMetrics) },
        clockSettings = flow { emit(clockSettings) },
        wallpaperSettings = flow { emit(wallpaperSettings) },
        mainDispatcher = dispatcher.value,
        ioDispatcher = dispatcher.value,
        onDraw = onDraw,
        onClearCanvas = onClearCanvas,
        getCurrentTimeMillis = getCurrentTimeMillis
    ).apply { initialize() }
    advanceUntilIdle()
    return engine
}

private val WallpaperEngineDelegateImpl.shouldBeVisible: Boolean
    get() = visibilityManager.state.shouldBeVisible
