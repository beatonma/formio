package org.beatonma.gclocks.android

import android.content.Context
import android.graphics.Canvas
import android.view.View
import org.beatonma.gclocks.clocks.createAnimatorFromOptions
import org.beatonma.gclocks.core.ClockAnimator
import org.beatonma.gclocks.core.geometry.MeasureConstraints
import org.beatonma.gclocks.core.options.Options

class ClockView(context: Context) : View(context) {
    var options: Options<*>? = null
        set(value) {
            field = value
            if (value != null) {
                animator = createAnimatorFromOptions(value, canvasHost.path) {
                    postInvalidate()
                }.apply {
                    setConstraints(constraints)
                }
            }

            invalidate()
        }
    private var animator: ClockAnimator<*, *>? = null
    private val canvasHost = AndroidCanvasHost()
    private var constraints: MeasureConstraints = MeasureConstraints(0f, 0f)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        constraints = MeasureConstraints(w.toFloat(), h.toFloat())
        animator?.setConstraints(constraints)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        animator?.let { animator ->
            animator.tick()
            canvasHost.withCanvas(canvas) { canvas ->
                animator.render(canvas)
            }
        }
    }
}