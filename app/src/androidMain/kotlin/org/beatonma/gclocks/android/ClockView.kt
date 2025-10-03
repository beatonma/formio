package org.beatonma.gclocks.android

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import org.beatonma.gclocks.clocks.createAnimatorFromOptions
import org.beatonma.gclocks.core.ClockAnimator
import org.beatonma.gclocks.core.GlyphState
import org.beatonma.gclocks.core.geometry.MeasureConstraints
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.util.debug


class ClockView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attributeSet, defStyleAttr, defStyleRes) {
    private var animator: ClockAnimator<*, *>? = null
    private val canvasHost = AndroidCanvasHost()
    private var constraints: MeasureConstraints = MeasureConstraints(0f, 0f)

    fun setOptions(options: Options<*>) {
        animator = createAnimatorFromOptions(options, canvasHost.path) {
            postInvalidate()
        }.apply {
            setConstraints(constraints)
        }

        postInvalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        constraints = MeasureConstraints(w.toFloat(), h.toFloat())
        animator?.setConstraints(constraints)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        constraints = MeasureConstraints(
            widthSize.toFloat(),
            heightSize.toFloat()
        )
        val measured = animator?.setConstraints(constraints)
            ?: return super.onMeasure(
                widthMeasureSpec,
                heightMeasureSpec
            )

        setMeasuredDimension(measured.width.toInt(), measured.height.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        animator?.let { animator ->
            animator.tick()
            canvasHost.withCanvas(canvas) { canvas ->
                animator.render(canvas)
            }
        } ?: debug("no animator")
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        animator?.getGlyphAt(event.x, event.y)?.setState(GlyphState.Active)
        return super.onTouchEvent(event)
    }
}
