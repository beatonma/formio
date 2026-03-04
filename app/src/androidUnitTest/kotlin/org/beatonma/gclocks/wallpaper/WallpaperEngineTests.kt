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

private val WallpaperEngineDelegateImpl.shouldBeVisible: Boolean
    get() = visibilityManager.state.value.shouldBeVisible


class WallpaperEngineTests {
    @Test
    fun `onVisibilityChanged is correct`() = runTest {
        createEngineDelegate().let { engine ->
            // Screen on, keyguard not locked
            engine.shouldBeVisible shouldbe true
            engine.visibilityManager.state.value

            expectWhenIdle({
                engine.onVisibilityChanged(isVisible = true, getIsKeyguardLocked = { false })
            }) {
                engine.shouldBeVisible shouldbe true
            }
        }

        createEngineDelegate().let { engine ->
            // Screen off, keyguard not locked
            engine.shouldBeVisible shouldbe true

            expectWhenIdle({
                engine.onVisibilityChanged(isVisible = false, getIsKeyguardLocked = { false })
            }) {
                engine.shouldBeVisible shouldbe false
            }
        }
        createEngineDelegate().let { engine ->
            // Screen off, keyguard locked
            engine.shouldBeVisible shouldbe true

            expectWhenIdle({
                engine.onVisibilityChanged(isVisible = false, getIsKeyguardLocked = { true })
            }
            ) {
                engine.shouldBeVisible shouldbe false
            }
        }
    }

    @Test
    fun `onVisibilityChanged with keyguard polling is correct`() = runTest {
        createEngineDelegate().let { engine ->
            // Screen on, keyguard initially locked, then unlocked
            engine.onVisibilityChanged(false, { true })
            var n = 0

            whenIdle {
                engine.onVisibilityChanged(isVisible = true, getIsKeyguardLocked = {
                    // simulate KeyguardManager polling: stay locked for 100ms
                    n += 10
                    n < 100
                })
            }

            n shouldbe 10
            engine.shouldBeVisible shouldbe false

            whenIdle {
                n shouldbe 100
                engine.shouldBeVisible shouldbe true
            }
        }
    }

    @Test
    fun `onOffsetsChanged is correct`() = runTest {
        createEngineDelegate(visibleOnLauncherPages = listOf()).let { engine ->
            WallpaperScroller(engine, totalPages = 2).let { scroller ->
                engine.shouldBeVisible shouldbe true

                expectWhenIdle({ scroller.scrollToNextPage() }) {
                    engine.shouldBeVisible shouldbe true
                }
                expectWhenIdle({ scroller.scrollToNextPage() }) {
                    engine.shouldBeVisible shouldbe true
                }
            }
        }

        createEngineDelegate(visibleOnLauncherPages = listOf(2)).let { engine ->
            WallpaperScroller(engine, totalPages = 2).let { scroller ->
                engine.shouldBeVisible shouldbe false

                expectWhenIdle({ scroller.scrollToNextPage() }) {
                    engine.shouldBeVisible shouldbe true
                }

                expectWhenIdle({ scroller.scrollToNextPage() }) {
                    engine.shouldBeVisible shouldbe false
                }
            }
        }

        createEngineDelegate(visibleOnLauncherPages = listOf(1, 3, 5)).let { engine ->
            WallpaperScroller(engine, totalPages = 5).let { scroller ->
                engine.shouldBeVisible shouldbe true
                expectWhenIdle({ scroller.scrollToNextPage() }) {
                    engine.shouldBeVisible shouldbe false
                }
                expectWhenIdle({ scroller.scrollToNextPage() }) {
                    engine.shouldBeVisible shouldbe true
                }
                expectWhenIdle({ scroller.scrollToNextPage() }) {
                    engine.shouldBeVisible shouldbe false
                }
                expectWhenIdle({ scroller.scrollToNextPage() }) {
                    engine.shouldBeVisible shouldbe true
                }

                // Back to first page
                expectWhenIdle({ scroller.scrollToNextPage() }) {
                    engine.shouldBeVisible shouldbe true
                }

                // Back to last page
                expectWhenIdle({ scroller.scrollToPreviousPage() }) {
                    engine.shouldBeVisible shouldbe true
                }
                expectWhenIdle({ scroller.scrollToPreviousPage() }) {
                    engine.shouldBeVisible shouldbe false
                }
                expectWhenIdle({ scroller.scrollToPreviousPage() }) {
                    engine.shouldBeVisible shouldbe true
                }
                expectWhenIdle({ scroller.scrollToPreviousPage() }) {
                    engine.shouldBeVisible shouldbe false
                }
                expectWhenIdle({ scroller.scrollToPreviousPage() }) {
                    engine.shouldBeVisible shouldbe true
                }
            }
        }
    }
}


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
    val dispatcher = StandardTestDispatcher(testScheduler)
    val engine = WallpaperEngineDelegateImpl(
        isPreview = false,
        isWallpaperVisible = true,
        displayMetrics = flow { emit(displayMetrics) },
        clockSettings = flow { emit(clockSettings) },
        wallpaperSettings = flow { emit(wallpaperSettings) },
        mainDispatcher = dispatcher,
        ioDispatcher = dispatcher,
        onDraw = onDraw,
        onClearCanvas = onClearCanvas,
        getCurrentTimeMillis = getCurrentTimeMillis
    )
    advanceUntilIdle()
    return engine
}


internal inline fun TestScope.whenIdle(block: TestScope.() -> Unit) {
    advanceUntilIdle()
    block()
}

/**
 * Do an [action], then test the state once coroutines have completed.
 */
internal inline fun TestScope.expectWhenIdle(action: () -> Unit, expectWhenIdle: TestScope.() -> Unit) {
    action()
    whenIdle(expectWhenIdle)
}