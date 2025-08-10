package org.beatonma.gclocks.io16

import org.beatonma.gclocks.core.BaseClockGlyph
import org.beatonma.gclocks.core.GlyphCompanion
import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.graphics.GenericCanvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.util.interpolate

private const val Width0 = 132f
private const val Width1 = 45f
private const val Width2 = 89f
private const val Width3 = 90f
private const val Width4 = 103f
private const val Width5 = 95f
private const val Width6 = 91f
private const val Width7 = 91f
private const val Width8 = 87f
private const val Width9 = 97f
private const val WidthSeparator = 16f


// TODO fix
private fun ease(t: Float) = t//accelerateDecelerate(overshoot(anticipate(t)))


class Io16Glyph(
    role: GlyphRole,
    scale: Float = 1f,
) : BaseClockGlyph(role, scale) {
    companion object : GlyphCompanion {
        override val maxSize = FloatSize(
            x = 132f,
            y = 132f,
        )
        override val aspectRatio: Float = maxSize.aspectRatio()
    }

    override val companion: GlyphCompanion = Io16Glyph

    override fun draw(
        canvas: GenericCanvas,
        glyphProgress: Float,
        paints: Paints,
    ) {
        canvas.beginPath()
        super.draw(canvas, ease(glyphProgress), paints)
    }

    override fun GenericCanvas.drawZeroOne(
        glyphProgress: Float,
        paints: Paints,
    ) {
        moveTo(
            interpolate(glyphProgress, 66.0f, 2.0f),
            interpolate(glyphProgress, 130f, 2f)
        )
        cubicTo(
            interpolate(glyphProgress, 101.0f, 2.0f),
            interpolate(glyphProgress, 130f, 2f),
            interpolate(glyphProgress, 130.0f, 2.0f),
            interpolate(glyphProgress, 101f, 2f),
            interpolate(glyphProgress, 130.0f, 2.0f),
            interpolate(glyphProgress, 66f, 2f)
        )
        cubicTo(
            interpolate(glyphProgress, 130.0f, 2.0f),
            interpolate(glyphProgress, 31f, 2f),
            interpolate(glyphProgress, 101.0f, 33.0f),
            interpolate(glyphProgress, 2f, 2f),
            interpolate(glyphProgress, 66.0f, 33.0f),
            interpolate(glyphProgress, 2f, 2f)
        )
        cubicTo(
            interpolate(glyphProgress, 31.0f, 33.0f),
            interpolate(glyphProgress, 2f, 2f),
            interpolate(glyphProgress, 2.0f, 33.0f),
            interpolate(glyphProgress, 31f, 130f),
            interpolate(glyphProgress, 2.0f, 33.0f),
            interpolate(glyphProgress, 66f, 130f)
        )
        cubicTo(
            interpolate(glyphProgress, 2.0f, 33.0f),
            interpolate(glyphProgress, 101f, 130f),
            interpolate(glyphProgress, 31.0f, 2.0f),
            interpolate(glyphProgress, 130f, 130f),
            interpolate(glyphProgress, 66.0f, 2.0f),
            interpolate(glyphProgress, 130f, 130f)
        )
        cubicTo(
            interpolate(glyphProgress, 66.0f, 2.0f),
            interpolate(glyphProgress, 130f, 130f),
            interpolate(glyphProgress, 66.0f, 2.0f),
            interpolate(glyphProgress, 130f, 2f),
            interpolate(glyphProgress, 66.0f, 2.0f),
            interpolate(glyphProgress, 130f, 2f)
        )
    }

    override fun GenericCanvas.drawOneTwo(
        glyphProgress: Float,
        paints: Paints,
    ) {
        moveTo(
            interpolate(glyphProgress, 2.0f, 87.0f),
            interpolate(glyphProgress, 2f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 87.0f),
            interpolate(glyphProgress, 2f, 130f),
            interpolate(glyphProgress, 2.0f, 20.0f),
            interpolate(glyphProgress, 2f, 130f),
            interpolate(glyphProgress, 2.0f, 20.0f),
            interpolate(glyphProgress, 2f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 20.0f),
            interpolate(glyphProgress, 2f, 130f),
            interpolate(glyphProgress, 2.0f, 30.0f),
            interpolate(glyphProgress, 2f, 122f),
            interpolate(glyphProgress, 2.0f, 36.0f),
            interpolate(glyphProgress, 2f, 117f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 42.0f),
            interpolate(glyphProgress, 2f, 111f),
            interpolate(glyphProgress, 2.0f, 49.0f),
            interpolate(glyphProgress, 2f, 105f),
            interpolate(glyphProgress, 2.0f, 67.0f),
            interpolate(glyphProgress, 2f, 88f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 71.0f),
            interpolate(glyphProgress, 2f, 84f),
            interpolate(glyphProgress, 2.0f, 74.0f),
            interpolate(glyphProgress, 2f, 80f),
            interpolate(glyphProgress, 2.0f, 77.0f),
            interpolate(glyphProgress, 2f, 77f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 85.0f),
            interpolate(glyphProgress, 2f, 66f),
            interpolate(glyphProgress, 2.0f, 86.0f),
            interpolate(glyphProgress, 2f, 59f),
            interpolate(glyphProgress, 2.0f, 86.0f),
            interpolate(glyphProgress, 2f, 49f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 87.0f),
            interpolate(glyphProgress, 2f, 36f),
            interpolate(glyphProgress, 2.0f, 73.0f),
            interpolate(glyphProgress, 2f, 19f),
            interpolate(glyphProgress, 2.0f, 50.0f),
            interpolate(glyphProgress, 2f, 21f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 27.0f),
            interpolate(glyphProgress, 2f, 22f),
            interpolate(glyphProgress, 2.0f, 20.0f),
            interpolate(glyphProgress, 2f, 44f),
            interpolate(glyphProgress, 2.0f, 20.0f),
            interpolate(glyphProgress, 2f, 44f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 20.0f),
            interpolate(glyphProgress, 2f, 44f),
            interpolate(glyphProgress, 2.0f, 2.0f),
            interpolate(glyphProgress, 2f, 25f),
            interpolate(glyphProgress, 2.0f, 2.0f),
            interpolate(glyphProgress, 2f, 25f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 2.0f),
            interpolate(glyphProgress, 2f, 25f),
            interpolate(glyphProgress, 2.0f, 9.0f),
            interpolate(glyphProgress, 2f, 4f),
            interpolate(glyphProgress, 2.0f, 32.0f),
            interpolate(glyphProgress, 2f, 2f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 55.0f),
            interpolate(glyphProgress, 2f, 0f),
            interpolate(glyphProgress, 2.0f, 69.0f),
            interpolate(glyphProgress, 2f, 18f),
            interpolate(glyphProgress, 2.0f, 68.0f),
            interpolate(glyphProgress, 2f, 30f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 68.0f),
            interpolate(glyphProgress, 2f, 43f),
            interpolate(glyphProgress, 2.0f, 67.0f),
            interpolate(glyphProgress, 2f, 51f),
            interpolate(glyphProgress, 2.0f, 49.0f),
            interpolate(glyphProgress, 2f, 69f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 47.0f),
            interpolate(glyphProgress, 2f, 71f),
            interpolate(glyphProgress, 2.0f, 44.0f),
            interpolate(glyphProgress, 2f, 74f),
            interpolate(glyphProgress, 2.0f, 42.0f),
            interpolate(glyphProgress, 2f, 76f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 29.0f),
            interpolate(glyphProgress, 2f, 88f),
            interpolate(glyphProgress, 2.0f, 22.0f),
            interpolate(glyphProgress, 2f, 94f),
            interpolate(glyphProgress, 2.0f, 17.0f),
            interpolate(glyphProgress, 2f, 99f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 11.0f),
            interpolate(glyphProgress, 2f, 103f),
            interpolate(glyphProgress, 33.0f, 2.0f),
            interpolate(glyphProgress, 2f, 111f),
            interpolate(glyphProgress, 33.0f, 2.0f),
            interpolate(glyphProgress, 2f, 111f)
        );
        cubicTo(
            interpolate(glyphProgress, 33.0f, 2.0f),
            interpolate(glyphProgress, 2f, 111f),
            interpolate(glyphProgress, 33.0f, 70.0f),
            interpolate(glyphProgress, 130f, 111f),
            interpolate(glyphProgress, 33.0f, 70.0f),
            interpolate(glyphProgress, 130f, 111f)
        );
        cubicTo(
            interpolate(glyphProgress, 33.0f, 70.0f),
            interpolate(glyphProgress, 130f, 111f),
            interpolate(glyphProgress, 2.0f, 87.0f),
            interpolate(glyphProgress, 130f, 130f),
            interpolate(glyphProgress, 2.0f, 87.0f),
            interpolate(glyphProgress, 130f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 87.0f),
            interpolate(glyphProgress, 130f, 130f),
            interpolate(glyphProgress, 2.0f, 87.0f),
            interpolate(glyphProgress, 2f, 130f),
            interpolate(glyphProgress, 2.0f, 87.0f),
            interpolate(glyphProgress, 2f, 130f)
        );
    }

    override fun GenericCanvas.drawTwoThree(
        glyphProgress: Float,
        paints: Paints,
    ) {
        moveTo(
            interpolate(glyphProgress, 87.0f, 20.0f),
            interpolate(glyphProgress, 130f, 104f)
        );
        cubicTo(
            interpolate(glyphProgress, 87.0f, 20.0f),
            interpolate(glyphProgress, 130f, 104f),
            interpolate(glyphProgress, 87.0f, 23.0f),
            interpolate(glyphProgress, 130f, 113f),
            interpolate(glyphProgress, 87.0f, 31.0f),
            interpolate(glyphProgress, 130f, 122f)
        );
        cubicTo(
            interpolate(glyphProgress, 87.0f, 40.0f),
            interpolate(glyphProgress, 130f, 130f),
            interpolate(glyphProgress, 87.0f, 59.0f),
            interpolate(glyphProgress, 130f, 134f),
            interpolate(glyphProgress, 87.0f, 73.0f),
            interpolate(glyphProgress, 130f, 125f)
        );
        cubicTo(
            interpolate(glyphProgress, 87.0f, 86.0f),
            interpolate(glyphProgress, 130f, 117f),
            interpolate(glyphProgress, 87.0f, 91.0f),
            interpolate(glyphProgress, 130f, 100f),
            interpolate(glyphProgress, 87.0f, 88.0f),
            interpolate(glyphProgress, 130f, 89f)
        );
        cubicTo(
            interpolate(glyphProgress, 87.0f, 84.0f),
            interpolate(glyphProgress, 130f, 74f),
            interpolate(glyphProgress, 87.0f, 71.0f),
            interpolate(glyphProgress, 130f, 72f),
            interpolate(glyphProgress, 87.0f, 71.0f),
            interpolate(glyphProgress, 130f, 72f)
        );
        cubicTo(
            interpolate(glyphProgress, 87.0f, 71.0f),
            interpolate(glyphProgress, 130f, 72f),
            interpolate(glyphProgress, 20.0f, 73.0f),
            interpolate(glyphProgress, 130f, 71f),
            interpolate(glyphProgress, 20.0f, 74.0f),
            interpolate(glyphProgress, 130f, 70f)
        );
        cubicTo(
            interpolate(glyphProgress, 20.0f, 78.0f),
            interpolate(glyphProgress, 130f, 67f),
            interpolate(glyphProgress, 30.0f, 85.0f),
            interpolate(glyphProgress, 122f, 60f),
            interpolate(glyphProgress, 36.0f, 85.0f),
            interpolate(glyphProgress, 117f, 50f)
        );
        cubicTo(
            interpolate(glyphProgress, 42.0f, 85.0f),
            interpolate(glyphProgress, 111f, 35f),
            interpolate(glyphProgress, 49.0f, 74.0f),
            interpolate(glyphProgress, 105f, 27f),
            interpolate(glyphProgress, 67.0f, 69.0f),
            interpolate(glyphProgress, 88f, 24f)
        );
        cubicTo(
            interpolate(glyphProgress, 71.0f, 65.0f),
            interpolate(glyphProgress, 84f, 22f),
            interpolate(glyphProgress, 74.0f, 56.0f),
            interpolate(glyphProgress, 80f, 19f),
            interpolate(glyphProgress, 77.0f, 42.0f),
            interpolate(glyphProgress, 77f, 22f)
        );
        cubicTo(
            interpolate(glyphProgress, 85.0f, 28.0f),
            interpolate(glyphProgress, 66f, 26f),
            interpolate(glyphProgress, 86.0f, 22.0f),
            interpolate(glyphProgress, 59f, 42f),
            interpolate(glyphProgress, 86.0f, 22.0f),
            interpolate(glyphProgress, 49f, 42f)
        );
        cubicTo(
            interpolate(glyphProgress, 87.0f, 22.0f),
            interpolate(glyphProgress, 36f, 42f),
            interpolate(glyphProgress, 73.0f, 4.0f),
            interpolate(glyphProgress, 19f, 24f),
            interpolate(glyphProgress, 50.0f, 4.0f),
            interpolate(glyphProgress, 21f, 24f)
        );
        cubicTo(
            interpolate(glyphProgress, 27.0f, 4.0f),
            interpolate(glyphProgress, 22f, 24f),
            interpolate(glyphProgress, 20.0f, 10.0f),
            interpolate(glyphProgress, 44f, 8f),
            interpolate(glyphProgress, 20.0f, 25.0f),
            interpolate(glyphProgress, 44f, 4f)
        );
        cubicTo(
            interpolate(glyphProgress, 20.0f, 38.0f),
            interpolate(glyphProgress, 44f, 0f),
            interpolate(glyphProgress, 2.0f, 48.0f),
            interpolate(glyphProgress, 25f, 4f),
            interpolate(glyphProgress, 2.0f, 53.0f),
            interpolate(glyphProgress, 25f, 6f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 57.0f),
            interpolate(glyphProgress, 25f, 9f),
            interpolate(glyphProgress, 9.0f, 67.0f),
            interpolate(glyphProgress, 4f, 17f),
            interpolate(glyphProgress, 32.0f, 67.0f),
            interpolate(glyphProgress, 2f, 32f)
        );
        cubicTo(
            interpolate(glyphProgress, 55.0f, 68.0f),
            interpolate(glyphProgress, 0f, 47f),
            interpolate(glyphProgress, 69.0f, 54.0f),
            interpolate(glyphProgress, 18f, 53f),
            interpolate(glyphProgress, 68.0f, 54.0f),
            interpolate(glyphProgress, 30f, 53f)
        );
        cubicTo(
            interpolate(glyphProgress, 68.0f, 54.0f),
            interpolate(glyphProgress, 43f, 53f),
            interpolate(glyphProgress, 67.0f, 58.0f),
            interpolate(glyphProgress, 51f, 56f),
            interpolate(glyphProgress, 49.0f, 63.0f),
            interpolate(glyphProgress, 69f, 60f)
        );
        cubicTo(
            interpolate(glyphProgress, 47.0f, 66.0f),
            interpolate(glyphProgress, 71f, 64f),
            interpolate(glyphProgress, 44.0f, 71.0f),
            interpolate(glyphProgress, 74f, 70f),
            interpolate(glyphProgress, 42.0f, 71.0f),
            interpolate(glyphProgress, 76f, 75f)
        );
        cubicTo(
            interpolate(glyphProgress, 29.0f, 71.0f),
            interpolate(glyphProgress, 88f, 87f),
            interpolate(glyphProgress, 22.0f, 68.0f),
            interpolate(glyphProgress, 94f, 98f),
            interpolate(glyphProgress, 17.0f, 55.0f),
            interpolate(glyphProgress, 99f, 106f)
        );
        cubicTo(
            interpolate(glyphProgress, 11.0f, 42.0f),
            interpolate(glyphProgress, 103f, 115f),
            interpolate(glyphProgress, 2.0f, 21.0f),
            interpolate(glyphProgress, 111f, 111f),
            interpolate(glyphProgress, 2.0f, 13.0f),
            interpolate(glyphProgress, 111f, 103f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 5.0f),
            interpolate(glyphProgress, 111f, 95f),
            interpolate(glyphProgress, 70.0f, 2.0f),
            interpolate(glyphProgress, 111f, 85f),
            interpolate(glyphProgress, 70.0f, 2.0f),
            interpolate(glyphProgress, 111f, 85f)
        );
        cubicTo(
            interpolate(glyphProgress, 70.0f, 2.0f),
            interpolate(glyphProgress, 111f, 85f),
            interpolate(glyphProgress, 87.0f, 20.0f),
            interpolate(glyphProgress, 130f, 104f),
            interpolate(glyphProgress, 87.0f, 20.0f),
            interpolate(glyphProgress, 130f, 104f)
        );
        cubicTo(
            interpolate(glyphProgress, 87.0f, 20.0f),
            interpolate(glyphProgress, 130f, 104f),
            interpolate(glyphProgress, 87.0f, 20.0f),
            interpolate(glyphProgress, 130f, 104f),
            interpolate(glyphProgress, 87.0f, 20.0f),
            interpolate(glyphProgress, 130f, 104f)
        );
    }

    override fun GenericCanvas.drawThreeFour(
        glyphProgress: Float,
        paints: Paints,
    ) {
        moveTo(
            interpolate(glyphProgress, 20.0f, 101.0f),
            interpolate(glyphProgress, 104f, 107f)
        );
        cubicTo(
            interpolate(glyphProgress, 20.0f, 101.0f),
            interpolate(glyphProgress, 104f, 107f),
            interpolate(glyphProgress, 23.0f, 101.0f),
            interpolate(glyphProgress, 113f, 107f),
            interpolate(glyphProgress, 31.0f, 101.0f),
            interpolate(glyphProgress, 122f, 107f)
        );
        cubicTo(
            interpolate(glyphProgress, 40.0f, 101.0f),
            interpolate(glyphProgress, 130f, 107f),
            interpolate(glyphProgress, 59.0f, 101.0f),
            interpolate(glyphProgress, 134f, 107f),
            interpolate(glyphProgress, 73.0f, 101.0f),
            interpolate(glyphProgress, 125f, 107f)
        );
        cubicTo(
            interpolate(glyphProgress, 86.0f, 101.0f),
            interpolate(glyphProgress, 117f, 107f),
            interpolate(glyphProgress, 91.0f, 101.0f),
            interpolate(glyphProgress, 100f, 107f),
            interpolate(glyphProgress, 88.0f, 101.0f),
            interpolate(glyphProgress, 89f, 107f)
        );
        cubicTo(
            interpolate(glyphProgress, 84.0f, 101.0f),
            interpolate(glyphProgress, 74f, 107f),
            interpolate(glyphProgress, 71.0f, 101.0f),
            interpolate(glyphProgress, 72f, 107f),
            interpolate(glyphProgress, 71.0f, 101.0f),
            interpolate(glyphProgress, 72f, 107f)
        );
        cubicTo(
            interpolate(glyphProgress, 71.0f, 101.0f),
            interpolate(glyphProgress, 72f, 107f),
            interpolate(glyphProgress, 73.0f, 101.0f),
            interpolate(glyphProgress, 71f, 107f),
            interpolate(glyphProgress, 74.0f, 101.0f),
            interpolate(glyphProgress, 70f, 107f)
        );
        cubicTo(
            interpolate(glyphProgress, 78.0f, 101.0f),
            interpolate(glyphProgress, 67f, 107f),
            interpolate(glyphProgress, 85.0f, 101.0f),
            interpolate(glyphProgress, 60f, 107f),
            interpolate(glyphProgress, 85.0f, 101.0f),
            interpolate(glyphProgress, 50f, 107f)
        );
        cubicTo(
            interpolate(glyphProgress, 85.0f, 101.0f),
            interpolate(glyphProgress, 35f, 107f),
            interpolate(glyphProgress, 74.0f, 101.0f),
            interpolate(glyphProgress, 27f, 107f),
            interpolate(glyphProgress, 69.0f, 101.0f),
            interpolate(glyphProgress, 24f, 107f)
        );
        cubicTo(
            interpolate(glyphProgress, 65.0f, 101.0f),
            interpolate(glyphProgress, 22f, 107f),
            interpolate(glyphProgress, 56.0f, 20.0f),
            interpolate(glyphProgress, 19f, 107f),
            interpolate(glyphProgress, 42.0f, 20.0f),
            interpolate(glyphProgress, 22f, 107f)
        );
        cubicTo(
            interpolate(glyphProgress, 28.0f, 20.0f),
            interpolate(glyphProgress, 26f, 107f),
            interpolate(glyphProgress, 22.0f, 20.0f),
            interpolate(glyphProgress, 42f, 96f),
            interpolate(glyphProgress, 22.0f, 20.0f),
            interpolate(glyphProgress, 42f, 96f)
        );
        cubicTo(
            interpolate(glyphProgress, 22.0f, 20.0f),
            interpolate(glyphProgress, 42f, 96f),
            interpolate(glyphProgress, 4.0f, 71.0f),
            interpolate(glyphProgress, 24f, 21f),
            interpolate(glyphProgress, 4.0f, 71.0f),
            interpolate(glyphProgress, 24f, 21f)
        );
        cubicTo(
            interpolate(glyphProgress, 4.0f, 71.0f),
            interpolate(glyphProgress, 24f, 21f),
            interpolate(glyphProgress, 10.0f, 87.0f),
            interpolate(glyphProgress, 8f, 21f),
            interpolate(glyphProgress, 25.0f, 87.0f),
            interpolate(glyphProgress, 4f, 21f)
        );
        cubicTo(
            interpolate(glyphProgress, 38.0f, 87.0f),
            interpolate(glyphProgress, 0f, 21f),
            interpolate(glyphProgress, 48.0f, 87.0f),
            interpolate(glyphProgress, 4f, 130f),
            interpolate(glyphProgress, 53.0f, 87.0f),
            interpolate(glyphProgress, 6f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 57.0f, 87.0f),
            interpolate(glyphProgress, 9f, 130f),
            interpolate(glyphProgress, 67.0f, 68.0f),
            interpolate(glyphProgress, 17f, 111f),
            interpolate(glyphProgress, 67.0f, 68.0f),
            interpolate(glyphProgress, 32f, 111f)
        );
        cubicTo(
            interpolate(glyphProgress, 68.0f, 68.0f),
            interpolate(glyphProgress, 47f, 111f),
            interpolate(glyphProgress, 54.0f, 68.0f),
            interpolate(glyphProgress, 53f, 2f),
            interpolate(glyphProgress, 54.0f, 68.0f),
            interpolate(glyphProgress, 53f, 2f)
        );
        cubicTo(
            interpolate(glyphProgress, 54.0f, 68.0f),
            interpolate(glyphProgress, 53f, 2f),
            interpolate(glyphProgress, 58.0f, 52.0f),
            interpolate(glyphProgress, 56f, 2f),
            interpolate(glyphProgress, 63.0f, 52.0f),
            interpolate(glyphProgress, 60f, 2f)
        );
        cubicTo(
            interpolate(glyphProgress, 66.0f, 52.0f),
            interpolate(glyphProgress, 64f, 2f),
            interpolate(glyphProgress, 71.0f, 2.0f),
            interpolate(glyphProgress, 70f, 77f),
            interpolate(glyphProgress, 71.0f, 2.0f),
            interpolate(glyphProgress, 75f, 77f)
        );
        cubicTo(
            interpolate(glyphProgress, 71.0f, 2.0f),
            interpolate(glyphProgress, 87f, 77f),
            interpolate(glyphProgress, 68.0f, 2.0f),
            interpolate(glyphProgress, 98f, 88f),
            interpolate(glyphProgress, 55.0f, 2.0f),
            interpolate(glyphProgress, 106f, 88f)
        );
        cubicTo(
            interpolate(glyphProgress, 42.0f, 2.0f),
            interpolate(glyphProgress, 115f, 88f),
            interpolate(glyphProgress, 21.0f, 44.0f),
            interpolate(glyphProgress, 111f, 88f),
            interpolate(glyphProgress, 13.0f, 44.0f),
            interpolate(glyphProgress, 103f, 88f)
        );
        cubicTo(
            interpolate(glyphProgress, 5.0f, 44.0f),
            interpolate(glyphProgress, 95f, 88f),
            interpolate(glyphProgress, 2.0f, 82.0f),
            interpolate(glyphProgress, 85f, 88f),
            interpolate(glyphProgress, 2.0f, 82.0f),
            interpolate(glyphProgress, 85f, 88f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 82.0f),
            interpolate(glyphProgress, 85f, 88f),
            interpolate(glyphProgress, 20.0f, 101.0f),
            interpolate(glyphProgress, 104f, 107f),
            interpolate(glyphProgress, 20.0f, 101.0f),
            interpolate(glyphProgress, 104f, 107f)
        );
        cubicTo(
            interpolate(glyphProgress, 20.0f, 101.0f),
            interpolate(glyphProgress, 104f, 107f),
            interpolate(glyphProgress, 20.0f, 101.0f),
            interpolate(glyphProgress, 104f, 107f),
            interpolate(glyphProgress, 20.0f, 101.0f),
            interpolate(glyphProgress, 104f, 107f)
        );
    }

    override fun GenericCanvas.drawFourFive(
        glyphProgress: Float,
        paints: Paints,
    ) {
        moveTo(
            interpolate(glyphProgress, 101.0f, 2.0f),
            interpolate(glyphProgress, 107f, 82f)
        );
        cubicTo(
            interpolate(glyphProgress, 101.0f, 2.0f),
            interpolate(glyphProgress, 107f, 82f),
            interpolate(glyphProgress, 101.0f, 20.0f),
            interpolate(glyphProgress, 107f, 101f),
            interpolate(glyphProgress, 101.0f, 20.0f),
            interpolate(glyphProgress, 107f, 101f)
        );
        cubicTo(
            interpolate(glyphProgress, 101.0f, 20.0f),
            interpolate(glyphProgress, 107f, 101f),
            interpolate(glyphProgress, 101.0f, 24.0f),
            interpolate(glyphProgress, 107f, 111f),
            interpolate(glyphProgress, 101.0f, 25.0f),
            interpolate(glyphProgress, 107f, 113f)
        );
        cubicTo(
            interpolate(glyphProgress, 101.0f, 25.0f),
            interpolate(glyphProgress, 107f, 114f),
            interpolate(glyphProgress, 101.0f, 35.0f),
            interpolate(glyphProgress, 107f, 130f),
            interpolate(glyphProgress, 101.0f, 56.0f),
            interpolate(glyphProgress, 107f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 101.0f, 77.0f),
            interpolate(glyphProgress, 107f, 130f),
            interpolate(glyphProgress, 101.0f, 93.0f),
            interpolate(glyphProgress, 107f, 114f),
            interpolate(glyphProgress, 101.0f, 93.0f),
            interpolate(glyphProgress, 107f, 94f)
        );
        cubicTo(
            interpolate(glyphProgress, 101.0f, 93.0f),
            interpolate(glyphProgress, 107f, 75f),
            interpolate(glyphProgress, 101.0f, 76.0f),
            interpolate(glyphProgress, 107f, 59f),
            interpolate(glyphProgress, 101.0f, 57.0f),
            interpolate(glyphProgress, 107f, 59f)
        );
        cubicTo(
            interpolate(glyphProgress, 101.0f, 56.0f),
            interpolate(glyphProgress, 107f, 59f),
            interpolate(glyphProgress, 20.0f, 54.0f),
            interpolate(glyphProgress, 107f, 59f),
            interpolate(glyphProgress, 20.0f, 53.0f),
            interpolate(glyphProgress, 107f, 59f)
        );
        cubicTo(
            interpolate(glyphProgress, 20.0f, 36.0f),
            interpolate(glyphProgress, 107f, 61f),
            interpolate(glyphProgress, 20.0f, 23.0f),
            interpolate(glyphProgress, 96f, 75f),
            interpolate(glyphProgress, 20.0f, 23.0f),
            interpolate(glyphProgress, 96f, 75f)
        );
        cubicTo(
            interpolate(glyphProgress, 20.0f, 23.0f),
            interpolate(glyphProgress, 96f, 75f),
            interpolate(glyphProgress, 71.0f, 30.0f),
            interpolate(glyphProgress, 21f, 21f),
            interpolate(glyphProgress, 71.0f, 30.0f),
            interpolate(glyphProgress, 21f, 21f)
        );
        cubicTo(
            interpolate(glyphProgress, 71.0f, 30.0f),
            interpolate(glyphProgress, 21f, 21f),
            interpolate(glyphProgress, 87.0f, 86.0f),
            interpolate(glyphProgress, 21f, 21f),
            interpolate(glyphProgress, 87.0f, 86.0f),
            interpolate(glyphProgress, 21f, 21f)
        );
        cubicTo(
            interpolate(glyphProgress, 87.0f, 86.0f),
            interpolate(glyphProgress, 21f, 21f),
            interpolate(glyphProgress, 87.0f, 68.0f),
            interpolate(glyphProgress, 130f, 2f),
            interpolate(glyphProgress, 87.0f, 68.0f),
            interpolate(glyphProgress, 130f, 2f)
        );
        cubicTo(
            interpolate(glyphProgress, 87.0f, 68.0f),
            interpolate(glyphProgress, 130f, 2f),
            interpolate(glyphProgress, 68.0f, 12.0f),
            interpolate(glyphProgress, 111f, 2f),
            interpolate(glyphProgress, 68.0f, 12.0f),
            interpolate(glyphProgress, 111f, 2f)
        );
        cubicTo(
            interpolate(glyphProgress, 68.0f, 12.0f),
            interpolate(glyphProgress, 111f, 2f),
            interpolate(glyphProgress, 68.0f, 5.0f),
            interpolate(glyphProgress, 2f, 56f),
            interpolate(glyphProgress, 68.0f, 5.0f),
            interpolate(glyphProgress, 2f, 56f)
        );
        cubicTo(
            interpolate(glyphProgress, 68.0f, 5.0f),
            interpolate(glyphProgress, 2f, 56f),
            interpolate(glyphProgress, 52.0f, 20.0f),
            interpolate(glyphProgress, 2f, 43f),
            interpolate(glyphProgress, 52.0f, 36.0f),
            interpolate(glyphProgress, 2f, 40f)
        );
        cubicTo(
            interpolate(glyphProgress, 52.0f, 37.0f),
            interpolate(glyphProgress, 2f, 40f),
            interpolate(glyphProgress, 2.0f, 38.0f),
            interpolate(glyphProgress, 77f, 40f),
            interpolate(glyphProgress, 2.0f, 39.0f),
            interpolate(glyphProgress, 77f, 40f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 58.0f),
            interpolate(glyphProgress, 77f, 40f),
            interpolate(glyphProgress, 2.0f, 75.0f),
            interpolate(glyphProgress, 88f, 56f),
            interpolate(glyphProgress, 2.0f, 75.0f),
            interpolate(glyphProgress, 88f, 75f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 75.0f),
            interpolate(glyphProgress, 88f, 95f),
            interpolate(glyphProgress, 44.0f, 59.0f),
            interpolate(glyphProgress, 88f, 111f),
            interpolate(glyphProgress, 44.0f, 38.0f),
            interpolate(glyphProgress, 88f, 111f)
        );
        cubicTo(
            interpolate(glyphProgress, 44.0f, 17.0f),
            interpolate(glyphProgress, 88f, 111f),
            interpolate(glyphProgress, 82.0f, 8.0f),
            interpolate(glyphProgress, 88f, 95f),
            interpolate(glyphProgress, 82.0f, 7.0f),
            interpolate(glyphProgress, 88f, 94f)
        );
        cubicTo(
            interpolate(glyphProgress, 82.0f, 6.0f),
            interpolate(glyphProgress, 88f, 92f),
            interpolate(glyphProgress, 101.0f, 2.0f),
            interpolate(glyphProgress, 107f, 82f),
            interpolate(glyphProgress, 101.0f, 2.0f),
            interpolate(glyphProgress, 107f, 82f)
        );
        cubicTo(
            interpolate(glyphProgress, 101.0f, 2.0f),
            interpolate(glyphProgress, 107f, 82f),
            interpolate(glyphProgress, 101.0f, 2.0f),
            interpolate(glyphProgress, 107f, 82f),
            interpolate(glyphProgress, 101.0f, 2.0f),
            interpolate(glyphProgress, 107f, 82f)
        );
    }

    override fun GenericCanvas.drawFiveSix(
        glyphProgress: Float,
        paints: Paints,
    ) {
        moveTo(
            interpolate(glyphProgress, 2.0f, 45.0f),
            interpolate(glyphProgress, 82f, 63f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 45.0f),
            interpolate(glyphProgress, 82f, 63f),
            interpolate(glyphProgress, 2.0f, 49.0f),
            interpolate(glyphProgress, 82f, 62f),
            interpolate(glyphProgress, 2.0f, 49.0f),
            interpolate(glyphProgress, 82f, 62f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 49.0f),
            interpolate(glyphProgress, 82f, 62f),
            interpolate(glyphProgress, 20.0f, 49.0f),
            interpolate(glyphProgress, 101f, 62f),
            interpolate(glyphProgress, 20.0f, 49.0f),
            interpolate(glyphProgress, 101f, 62f)
        );
        cubicTo(
            interpolate(glyphProgress, 20.0f, 53.0f),
            interpolate(glyphProgress, 101f, 61f),
            interpolate(glyphProgress, 24.0f, 61.0f),
            interpolate(glyphProgress, 111f, 60f),
            interpolate(glyphProgress, 25.0f, 71.0f),
            interpolate(glyphProgress, 113f, 64f)
        );
        cubicTo(
            interpolate(glyphProgress, 25.0f, 85.0f),
            interpolate(glyphProgress, 114f, 71f),
            interpolate(glyphProgress, 35.0f, 90.0f),
            interpolate(glyphProgress, 130f, 86f),
            interpolate(glyphProgress, 56.0f, 89.0f),
            interpolate(glyphProgress, 130f, 96f)
        );
        cubicTo(
            interpolate(glyphProgress, 77.0f, 89.0f),
            interpolate(glyphProgress, 130f, 106f),
            interpolate(glyphProgress, 93.0f, 79.0f),
            interpolate(glyphProgress, 114f, 125f),
            interpolate(glyphProgress, 93.0f, 60.0f),
            interpolate(glyphProgress, 94f, 129f)
        );
        cubicTo(
            interpolate(glyphProgress, 93.0f, 56.0f),
            interpolate(glyphProgress, 75f, 130f),
            interpolate(glyphProgress, 76.0f, 53.0f),
            interpolate(glyphProgress, 59f, 130f),
            interpolate(glyphProgress, 57.0f, 49.0f),
            interpolate(glyphProgress, 59f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 56.0f, 36.0f),
            interpolate(glyphProgress, 59f, 128f),
            interpolate(glyphProgress, 54.0f, 24.0f),
            interpolate(glyphProgress, 59f, 118f),
            interpolate(glyphProgress, 53.0f, 19.0f),
            interpolate(glyphProgress, 59f, 106f)
        );
        cubicTo(
            interpolate(glyphProgress, 36.0f, 14.0f),
            interpolate(glyphProgress, 61f, 91f),
            interpolate(glyphProgress, 23.0f, 22.0f),
            interpolate(glyphProgress, 75f, 75f),
            interpolate(glyphProgress, 23.0f, 26.0f),
            interpolate(glyphProgress, 75f, 69f)
        );
        cubicTo(
            interpolate(glyphProgress, 23.0f, 29.0f),
            interpolate(glyphProgress, 75f, 62f),
            interpolate(glyphProgress, 30.0f, 58.0f),
            interpolate(glyphProgress, 21f, 20f),
            interpolate(glyphProgress, 30.0f, 58.0f),
            interpolate(glyphProgress, 21f, 20f)
        );
        cubicTo(
            interpolate(glyphProgress, 30.0f, 58.0f),
            interpolate(glyphProgress, 21f, 20f),
            interpolate(glyphProgress, 86.0f, 47.0f),
            interpolate(glyphProgress, 21f, 9f),
            interpolate(glyphProgress, 86.0f, 47.0f),
            interpolate(glyphProgress, 21f, 9f)
        );
        cubicTo(
            interpolate(glyphProgress, 86.0f, 47.0f),
            interpolate(glyphProgress, 21f, 9f),
            interpolate(glyphProgress, 68.0f, 41.0f),
            interpolate(glyphProgress, 2f, 2f),
            interpolate(glyphProgress, 68.0f, 41.0f),
            interpolate(glyphProgress, 2f, 2f)
        );
        cubicTo(
            interpolate(glyphProgress, 68.0f, 41.0f),
            interpolate(glyphProgress, 2f, 2f),
            interpolate(glyphProgress, 12.0f, 12.0f),
            interpolate(glyphProgress, 2f, 43f),
            interpolate(glyphProgress, 12.0f, 8.0f),
            interpolate(glyphProgress, 2f, 50f)
        );
        cubicTo(
            interpolate(glyphProgress, 12.0f, 5.0f),
            interpolate(glyphProgress, 2f, 57f),
            interpolate(glyphProgress, 5.0f, -4.0f),
            interpolate(glyphProgress, 56f, 73f),
            interpolate(glyphProgress, 5.0f, 2.0f),
            interpolate(glyphProgress, 56f, 88f)
        );
        cubicTo(
            interpolate(glyphProgress, 5.0f, 7.0f),
            interpolate(glyphProgress, 56f, 102f),
            interpolate(glyphProgress, 20.0f, 22.0f),
            interpolate(glyphProgress, 43f, 112f),
            interpolate(glyphProgress, 36.0f, 39.0f),
            interpolate(glyphProgress, 40f, 111f)
        );
        cubicTo(
            interpolate(glyphProgress, 37.0f, 40.0f),
            interpolate(glyphProgress, 40f, 111f),
            interpolate(glyphProgress, 38.0f, 41.0f),
            interpolate(glyphProgress, 40f, 111f),
            interpolate(glyphProgress, 39.0f, 43.0f),
            interpolate(glyphProgress, 40f, 111f)
        );
        cubicTo(
            interpolate(glyphProgress, 58.0f, 62.0f),
            interpolate(glyphProgress, 40f, 107f),
            interpolate(glyphProgress, 75.0f, 71.0f),
            interpolate(glyphProgress, 56f, 88f),
            interpolate(glyphProgress, 75.0f, 72.0f),
            interpolate(glyphProgress, 75f, 78f)
        );
        cubicTo(
            interpolate(glyphProgress, 75.0f, 72.0f),
            interpolate(glyphProgress, 95f, 67f),
            interpolate(glyphProgress, 59.0f, 68.0f),
            interpolate(glyphProgress, 111f, 53f),
            interpolate(glyphProgress, 38.0f, 53.0f),
            interpolate(glyphProgress, 111f, 46f)
        );
        cubicTo(
            interpolate(glyphProgress, 17.0f, 41.0f),
            interpolate(glyphProgress, 111f, 40f),
            interpolate(glyphProgress, 8.0f, 27.0f),
            interpolate(glyphProgress, 95f, 44f),
            interpolate(glyphProgress, 7.0f, 27.0f),
            interpolate(glyphProgress, 94f, 44f)
        );
        cubicTo(
            interpolate(glyphProgress, 6.0f, 27.0f),
            interpolate(glyphProgress, 92f, 44f),
            interpolate(glyphProgress, 2.0f, 45.0f),
            interpolate(glyphProgress, 82f, 63f),
            interpolate(glyphProgress, 2.0f, 45.0f),
            interpolate(glyphProgress, 82f, 63f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 45.0f),
            interpolate(glyphProgress, 82f, 63f),
            interpolate(glyphProgress, 2.0f, 45.0f),
            interpolate(glyphProgress, 82f, 63f),
            interpolate(glyphProgress, 2.0f, 45.0f),
            interpolate(glyphProgress, 82f, 63f)
        );
    }

    override fun GenericCanvas.drawSixSeven(
        glyphProgress: Float,
        paints: Paints,
    ) {
        moveTo(
            interpolate(glyphProgress, 45.0f, 37.0f),
            interpolate(glyphProgress, 63f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 45.0f, 37.0f),
            interpolate(glyphProgress, 63f, 130f),
            interpolate(glyphProgress, 49.0f, 37.0f),
            interpolate(glyphProgress, 62f, 130f),
            interpolate(glyphProgress, 49.0f, 37.0f),
            interpolate(glyphProgress, 62f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 49.0f, 37.0f),
            interpolate(glyphProgress, 62f, 130f),
            interpolate(glyphProgress, 49.0f, 37.0f),
            interpolate(glyphProgress, 62f, 130f),
            interpolate(glyphProgress, 49.0f, 37.0f),
            interpolate(glyphProgress, 62f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 53.0f, 37.0f),
            interpolate(glyphProgress, 61f, 130f),
            interpolate(glyphProgress, 61.0f, 37.0f),
            interpolate(glyphProgress, 60f, 130f),
            interpolate(glyphProgress, 71.0f, 37.0f),
            interpolate(glyphProgress, 64f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 85.0f, 37.0f),
            interpolate(glyphProgress, 71f, 130f),
            interpolate(glyphProgress, 90.0f, 37.0f),
            interpolate(glyphProgress, 86f, 130f),
            interpolate(glyphProgress, 89.0f, 37.0f),
            interpolate(glyphProgress, 96f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 89.0f, 37.0f),
            interpolate(glyphProgress, 106f, 130f),
            interpolate(glyphProgress, 79.0f, 37.0f),
            interpolate(glyphProgress, 125f, 130f),
            interpolate(glyphProgress, 60.0f, 37.0f),
            interpolate(glyphProgress, 129f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 56.0f, 37.0f),
            interpolate(glyphProgress, 130f, 130f),
            interpolate(glyphProgress, 53.0f, 37.0f),
            interpolate(glyphProgress, 130f, 130f),
            interpolate(glyphProgress, 49.0f, 37.0f),
            interpolate(glyphProgress, 130f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 36.0f, 37.0f),
            interpolate(glyphProgress, 128f, 130f),
            interpolate(glyphProgress, 24.0f, 37.0f),
            interpolate(glyphProgress, 118f, 130f),
            interpolate(glyphProgress, 19.0f, 37.0f),
            interpolate(glyphProgress, 106f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 14.0f, 37.0f),
            interpolate(glyphProgress, 91f, 130f),
            interpolate(glyphProgress, 22.0f, 37.0f),
            interpolate(glyphProgress, 75f, 130f),
            interpolate(glyphProgress, 26.0f, 37.0f),
            interpolate(glyphProgress, 69f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 29.0f, 37.0f),
            interpolate(glyphProgress, 62f, 130f),
            interpolate(glyphProgress, 58.0f, 37.0f),
            interpolate(glyphProgress, 20f, 130f),
            interpolate(glyphProgress, 58.0f, 37.0f),
            interpolate(glyphProgress, 20f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 58.0f, 37.0f),
            interpolate(glyphProgress, 20f, 130f),
            interpolate(glyphProgress, 47.0f, 37.0f),
            interpolate(glyphProgress, 9f, 130f),
            interpolate(glyphProgress, 47.0f, 37.0f),
            interpolate(glyphProgress, 9f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 47.0f, 37.0f),
            interpolate(glyphProgress, 9f, 130f),
            interpolate(glyphProgress, 41.0f, 89.0f),
            interpolate(glyphProgress, 2f, 35f),
            interpolate(glyphProgress, 41.0f, 89.0f),
            interpolate(glyphProgress, 2f, 35f)
        );
        cubicTo(
            interpolate(glyphProgress, 41.0f, 89.0f),
            interpolate(glyphProgress, 2f, 35f),
            interpolate(glyphProgress, 12.0f, 89.0f),
            interpolate(glyphProgress, 43f, 21f),
            interpolate(glyphProgress, 8.0f, 89.0f),
            interpolate(glyphProgress, 50f, 21f)
        );
        cubicTo(
            interpolate(glyphProgress, 5.0f, 89.0f),
            interpolate(glyphProgress, 57f, 21f),
            interpolate(glyphProgress, -4.0f, 20.0f),
            interpolate(glyphProgress, 73f, 21f),
            interpolate(glyphProgress, 2.0f, 20.0f),
            interpolate(glyphProgress, 88f, 21f)
        );
        cubicTo(
            interpolate(glyphProgress, 7.0f, 20.0f),
            interpolate(glyphProgress, 102f, 21f),
            interpolate(glyphProgress, 22.0f, 2.0f),
            interpolate(glyphProgress, 112f, 2f),
            interpolate(glyphProgress, 39.0f, 2.0f),
            interpolate(glyphProgress, 111f, 2f)
        );
        cubicTo(
            interpolate(glyphProgress, 40.0f, 2.0f),
            interpolate(glyphProgress, 111f, 2f),
            interpolate(glyphProgress, 41.0f, 38.0f),
            interpolate(glyphProgress, 111f, 2f),
            interpolate(glyphProgress, 43.0f, 38.0f),
            interpolate(glyphProgress, 111f, 2f)
        );
        cubicTo(
            interpolate(glyphProgress, 62.0f, 38.0f),
            interpolate(glyphProgress, 107f, 2f),
            interpolate(glyphProgress, 71.0f, 71.0f),
            interpolate(glyphProgress, 88f, 2f),
            interpolate(glyphProgress, 72.0f, 71.0f),
            interpolate(glyphProgress, 78f, 2f)
        );
        cubicTo(
            interpolate(glyphProgress, 72.0f, 71.0f),
            interpolate(glyphProgress, 67f, 2f),
            interpolate(glyphProgress, 68.0f, 71.0f),
            interpolate(glyphProgress, 53f, 17f),
            interpolate(glyphProgress, 53.0f, 71.0f),
            interpolate(glyphProgress, 46f, 17f)
        );
        cubicTo(
            interpolate(glyphProgress, 41.0f, 71.0f),
            interpolate(glyphProgress, 40f, 17f),
            interpolate(glyphProgress, 27.0f, 19.0f),
            interpolate(glyphProgress, 44f, 111f),
            interpolate(glyphProgress, 27.0f, 19.0f),
            interpolate(glyphProgress, 44f, 111f)
        );
        cubicTo(
            interpolate(glyphProgress, 27.0f, 19.0f),
            interpolate(glyphProgress, 44f, 111f),
            interpolate(glyphProgress, 45.0f, 37.0f),
            interpolate(glyphProgress, 63f, 130f),
            interpolate(glyphProgress, 45.0f, 37.0f),
            interpolate(glyphProgress, 63f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 45.0f, 37.0f),
            interpolate(glyphProgress, 63f, 130f),
            interpolate(glyphProgress, 45.0f, 37.0f),
            interpolate(glyphProgress, 63f, 130f),
            interpolate(glyphProgress, 45.0f, 37.0f),
            interpolate(glyphProgress, 63f, 130f)
        );
    }

    override fun GenericCanvas.drawSevenEight(
        glyphProgress: Float,
        paints: Paints,
    ) {
        moveTo(
            interpolate(glyphProgress, 37.0f, 4.0f),
            interpolate(glyphProgress, 130f, 83f)
        );
        cubicTo(
            interpolate(glyphProgress, 37.0f, 5.0f),
            interpolate(glyphProgress, 130f, 81f),
            interpolate(glyphProgress, 37.0f, 6.0f),
            interpolate(glyphProgress, 130f, 77f),
            interpolate(glyphProgress, 37.0f, 8.0f),
            interpolate(glyphProgress, 130f, 75f)
        );
        cubicTo(
            interpolate(glyphProgress, 37.0f, 11.0f),
            interpolate(glyphProgress, 130f, 70f),
            interpolate(glyphProgress, 37.0f, 16.0f),
            interpolate(glyphProgress, 130f, 66f),
            interpolate(glyphProgress, 37.0f, 22.0f),
            interpolate(glyphProgress, 130f, 63f)
        );
        cubicTo(
            interpolate(glyphProgress, 37.0f, 22.0f),
            interpolate(glyphProgress, 130f, 63f),
            interpolate(glyphProgress, 37.0f, 22.0f),
            interpolate(glyphProgress, 130f, 62f),
            interpolate(glyphProgress, 37.0f, 22.0f),
            interpolate(glyphProgress, 130f, 62f)
        );
        cubicTo(
            interpolate(glyphProgress, 37.0f, 17.0f),
            interpolate(glyphProgress, 130f, 59f),
            interpolate(glyphProgress, 37.0f, 14.0f),
            interpolate(glyphProgress, 130f, 56f),
            interpolate(glyphProgress, 37.0f, 11.0f),
            interpolate(glyphProgress, 130f, 51f)
        );
        cubicTo(
            interpolate(glyphProgress, 37.0f, 8.0f),
            interpolate(glyphProgress, 130f, 46f),
            interpolate(glyphProgress, 37.0f, 6.0f),
            interpolate(glyphProgress, 130f, 42f),
            interpolate(glyphProgress, 37.0f, 6.0f),
            interpolate(glyphProgress, 130f, 37f)
        );
        cubicTo(
            interpolate(glyphProgress, 37.0f, 6.0f),
            interpolate(glyphProgress, 130f, 30f),
            interpolate(glyphProgress, 37.0f, 9.0f),
            interpolate(glyphProgress, 130f, 22f),
            interpolate(glyphProgress, 37.0f, 12.0f),
            interpolate(glyphProgress, 130f, 17f)
        );
        cubicTo(
            interpolate(glyphProgress, 37.0f, 13.0f),
            interpolate(glyphProgress, 130f, 15f),
            interpolate(glyphProgress, 37.0f, 15.0f),
            interpolate(glyphProgress, 130f, 14f),
            interpolate(glyphProgress, 37.0f, 17.0f),
            interpolate(glyphProgress, 130f, 12f)
        );
        cubicTo(
            interpolate(glyphProgress, 37.0f, 24.0f),
            interpolate(glyphProgress, 130f, 5f),
            interpolate(glyphProgress, 37.0f, 33.0f),
            interpolate(glyphProgress, 130f, 2f),
            interpolate(glyphProgress, 37.0f, 43.0f),
            interpolate(glyphProgress, 130f, 2f)
        );
        cubicTo(
            interpolate(glyphProgress, 37.0f, 54.0f),
            interpolate(glyphProgress, 130f, 2f),
            interpolate(glyphProgress, 37.0f, 63.0f),
            interpolate(glyphProgress, 130f, 5f),
            interpolate(glyphProgress, 37.0f, 70.0f),
            interpolate(glyphProgress, 130f, 12f)
        );
        cubicTo(
            interpolate(glyphProgress, 37.0f, 77.0f),
            interpolate(glyphProgress, 130f, 19f),
            interpolate(glyphProgress, 37.0f, 81.0f),
            interpolate(glyphProgress, 130f, 27f),
            interpolate(glyphProgress, 37.0f, 81.0f),
            interpolate(glyphProgress, 130f, 37f)
        );
        cubicTo(
            interpolate(glyphProgress, 37.0f, 81.0f),
            interpolate(glyphProgress, 130f, 39f),
            interpolate(glyphProgress, 37.0f, 81.0f),
            interpolate(glyphProgress, 130f, 41f),
            interpolate(glyphProgress, 37.0f, 80.0f),
            interpolate(glyphProgress, 130f, 43f)
        );
        cubicTo(
            interpolate(glyphProgress, 37.0f, 79.0f),
            interpolate(glyphProgress, 130f, 45f),
            interpolate(glyphProgress, 37.0f, 78.0f),
            interpolate(glyphProgress, 130f, 49f),
            interpolate(glyphProgress, 37.0f, 76.0f),
            interpolate(glyphProgress, 130f, 51f)
        );
        cubicTo(
            interpolate(glyphProgress, 37.0f, 73.0f),
            interpolate(glyphProgress, 130f, 56f),
            interpolate(glyphProgress, 37.0f, 70.0f),
            interpolate(glyphProgress, 130f, 59f),
            interpolate(glyphProgress, 37.0f, 65.0f),
            interpolate(glyphProgress, 130f, 62f)
        );
        cubicTo(
            interpolate(glyphProgress, 37.0f, 65.0f),
            interpolate(glyphProgress, 130f, 62f),
            interpolate(glyphProgress, 89.0f, 65.0f),
            interpolate(glyphProgress, 35f, 63f),
            interpolate(glyphProgress, 89.0f, 65.0f),
            interpolate(glyphProgress, 35f, 63f)
        );
        cubicTo(
            interpolate(glyphProgress, 89.0f, 71.0f),
            interpolate(glyphProgress, 35f, 66f),
            interpolate(glyphProgress, 89.0f, 76.0f),
            interpolate(glyphProgress, 21f, 70f),
            interpolate(glyphProgress, 89.0f, 79.0f),
            interpolate(glyphProgress, 21f, 75f)
        );
        cubicTo(
            interpolate(glyphProgress, 89.0f, 83.0f),
            interpolate(glyphProgress, 21f, 81f),
            interpolate(glyphProgress, 20.0f, 85.0f),
            interpolate(glyphProgress, 21f, 87f),
            interpolate(glyphProgress, 20.0f, 85.0f),
            interpolate(glyphProgress, 21f, 93f)
        );
        cubicTo(
            interpolate(glyphProgress, 20.0f, 85.0f),
            interpolate(glyphProgress, 21f, 101f),
            interpolate(glyphProgress, 2.0f, 81.0f),
            interpolate(glyphProgress, 2f, 110f),
            interpolate(glyphProgress, 2.0f, 77.0f),
            interpolate(glyphProgress, 2f, 116f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 76.0f),
            interpolate(glyphProgress, 2f, 117f),
            interpolate(glyphProgress, 38.0f, 74.0f),
            interpolate(glyphProgress, 2f, 118f),
            interpolate(glyphProgress, 38.0f, 73.0f),
            interpolate(glyphProgress, 2f, 119f)
        );
        cubicTo(
            interpolate(glyphProgress, 38.0f, 65.0f),
            interpolate(glyphProgress, 2f, 126f),
            interpolate(glyphProgress, 71.0f, 55.0f),
            interpolate(glyphProgress, 2f, 130f),
            interpolate(glyphProgress, 71.0f, 43.0f),
            interpolate(glyphProgress, 2f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 71.0f, 32.0f),
            interpolate(glyphProgress, 2f, 130f),
            interpolate(glyphProgress, 71.0f, 22.0f),
            interpolate(glyphProgress, 17f, 126f),
            interpolate(glyphProgress, 71.0f, 14.0f),
            interpolate(glyphProgress, 17f, 119f)
        );
        cubicTo(
            interpolate(glyphProgress, 71.0f, 6.0f),
            interpolate(glyphProgress, 17f, 112f),
            interpolate(glyphProgress, 19.0f, 2.0f),
            interpolate(glyphProgress, 111f, 104f),
            interpolate(glyphProgress, 19.0f, 2.0f),
            interpolate(glyphProgress, 111f, 93f)
        );
        cubicTo(
            interpolate(glyphProgress, 19.0f, 2.0f),
            interpolate(glyphProgress, 111f, 90f),
            interpolate(glyphProgress, 37.0f, 3.0f),
            interpolate(glyphProgress, 130f, 86f),
            interpolate(glyphProgress, 37.0f, 4.0f),
            interpolate(glyphProgress, 130f, 83f)
        );
        cubicTo(
            interpolate(glyphProgress, 37.0f, 4.0f),
            interpolate(glyphProgress, 130f, 83f),
            interpolate(glyphProgress, 37.0f, 4.0f),
            interpolate(glyphProgress, 130f, 83f),
            interpolate(glyphProgress, 37.0f, 4.0f),
            interpolate(glyphProgress, 130f, 83f)
        );
    }

    override fun GenericCanvas.drawEightNine(
        glyphProgress: Float,
        paints: Paints,
    ) {
        moveTo(
            interpolate(glyphProgress, 4.0f, 44.0f),
            interpolate(glyphProgress, 83f, 123f)
        );
        cubicTo(
            interpolate(glyphProgress, 5.0f, 44.0f),
            interpolate(glyphProgress, 81f, 123f),
            interpolate(glyphProgress, 6.0f, 44.0f),
            interpolate(glyphProgress, 77f, 123f),
            interpolate(glyphProgress, 8.0f, 44.0f),
            interpolate(glyphProgress, 75f, 123f)
        );
        cubicTo(
            interpolate(glyphProgress, 11.0f, 44.0f),
            interpolate(glyphProgress, 70f, 123f),
            interpolate(glyphProgress, 16.0f, 44.0f),
            interpolate(glyphProgress, 66f, 123f),
            interpolate(glyphProgress, 22.0f, 44.0f),
            interpolate(glyphProgress, 63f, 123f)
        );
        cubicTo(
            interpolate(glyphProgress, 22.0f, 44.0f),
            interpolate(glyphProgress, 63f, 123f),
            interpolate(glyphProgress, 22.0f, 44.0f),
            interpolate(glyphProgress, 62f, 123f),
            interpolate(glyphProgress, 22.0f, 44.0f),
            interpolate(glyphProgress, 62f, 123f)
        );
        cubicTo(
            interpolate(glyphProgress, 17.0f, 44.0f),
            interpolate(glyphProgress, 59f, 123f),
            interpolate(glyphProgress, 14.0f, 51.0f),
            interpolate(glyphProgress, 56f, 130f),
            interpolate(glyphProgress, 11.0f, 51.0f),
            interpolate(glyphProgress, 51f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 8.0f, 51.0f),
            interpolate(glyphProgress, 46f, 130f),
            interpolate(glyphProgress, 6.0f, 79.0f),
            interpolate(glyphProgress, 42f, 89f),
            interpolate(glyphProgress, 6.0f, 83.0f),
            interpolate(glyphProgress, 37f, 82f)
        );
        cubicTo(
            interpolate(glyphProgress, 6.0f, 86.0f),
            interpolate(glyphProgress, 30f, 75f),
            interpolate(glyphProgress, 9.0f, 95.0f),
            interpolate(glyphProgress, 22f, 59f),
            interpolate(glyphProgress, 12.0f, 89.0f),
            interpolate(glyphProgress, 17f, 44f)
        );
        cubicTo(
            interpolate(glyphProgress, 13.0f, 84.0f),
            interpolate(glyphProgress, 15f, 30f),
            interpolate(glyphProgress, 15.0f, 69.0f),
            interpolate(glyphProgress, 14f, 20f),
            interpolate(glyphProgress, 17.0f, 52.0f),
            interpolate(glyphProgress, 12f, 21f)
        );
        cubicTo(
            interpolate(glyphProgress, 24.0f, 51.0f),
            interpolate(glyphProgress, 5f, 21f),
            interpolate(glyphProgress, 33.0f, 50.0f),
            interpolate(glyphProgress, 2f, 21f),
            interpolate(glyphProgress, 43.0f, 49.0f),
            interpolate(glyphProgress, 2f, 21f)
        );
        cubicTo(
            interpolate(glyphProgress, 54.0f, 30.0f),
            interpolate(glyphProgress, 2f, 25f),
            interpolate(glyphProgress, 63.0f, 20.0f),
            interpolate(glyphProgress, 5f, 44f),
            interpolate(glyphProgress, 70.0f, 20.0f),
            interpolate(glyphProgress, 12f, 54f)
        );
        cubicTo(
            interpolate(glyphProgress, 77.0f, 19.0f),
            interpolate(glyphProgress, 19f, 65f),
            interpolate(glyphProgress, 81.0f, 23.0f),
            interpolate(glyphProgress, 27f, 79f),
            interpolate(glyphProgress, 81.0f, 38.0f),
            interpolate(glyphProgress, 37f, 86f)
        );
        cubicTo(
            interpolate(glyphProgress, 81.0f, 50.0f),
            interpolate(glyphProgress, 39f, 92f),
            interpolate(glyphProgress, 81.0f, 64.0f),
            interpolate(glyphProgress, 41f, 88f),
            interpolate(glyphProgress, 80.0f, 64.0f),
            interpolate(glyphProgress, 43f, 88f)
        );
        cubicTo(
            interpolate(glyphProgress, 79.0f, 64.0f),
            interpolate(glyphProgress, 45f, 88f),
            interpolate(glyphProgress, 78.0f, 47.0f),
            interpolate(glyphProgress, 49f, 69f),
            interpolate(glyphProgress, 76.0f, 47.0f),
            interpolate(glyphProgress, 51f, 69f)
        );
        cubicTo(
            interpolate(glyphProgress, 73.0f, 47.0f),
            interpolate(glyphProgress, 56f, 69f),
            interpolate(glyphProgress, 70.0f, 43.0f),
            interpolate(glyphProgress, 59f, 70f),
            interpolate(glyphProgress, 65.0f, 42.0f),
            interpolate(glyphProgress, 62f, 70f)
        );
        cubicTo(
            interpolate(glyphProgress, 65.0f, 42.0f),
            interpolate(glyphProgress, 62f, 70f),
            interpolate(glyphProgress, 65.0f, 43.0f),
            interpolate(glyphProgress, 63f, 70f),
            interpolate(glyphProgress, 65.0f, 43.0f),
            interpolate(glyphProgress, 63f, 70f)
        );
        cubicTo(
            interpolate(glyphProgress, 71.0f, 38.0f),
            interpolate(glyphProgress, 66f, 71f),
            interpolate(glyphProgress, 76.0f, 30.0f),
            interpolate(glyphProgress, 70f, 72f),
            interpolate(glyphProgress, 79.0f, 20.0f),
            interpolate(glyphProgress, 75f, 68f)
        );
        cubicTo(
            interpolate(glyphProgress, 83.0f, 6.0f),
            interpolate(glyphProgress, 81f, 61f),
            interpolate(glyphProgress, 85.0f, 2.0f),
            interpolate(glyphProgress, 87f, 46f),
            interpolate(glyphProgress, 85.0f, 2.0f),
            interpolate(glyphProgress, 93f, 36f)
        );
        cubicTo(
            interpolate(glyphProgress, 85.0f, 3.0f),
            interpolate(glyphProgress, 101f, 26f),
            interpolate(glyphProgress, 81.0f, 12.0f),
            interpolate(glyphProgress, 110f, 7f),
            interpolate(glyphProgress, 77.0f, 31.0f),
            interpolate(glyphProgress, 116f, 3f)
        );
        cubicTo(
            interpolate(glyphProgress, 76.0f, 35.0f),
            interpolate(glyphProgress, 117f, 2f),
            interpolate(glyphProgress, 74.0f, 38.0f),
            interpolate(glyphProgress, 118f, 2f),
            interpolate(glyphProgress, 73.0f, 42.0f),
            interpolate(glyphProgress, 119f, 2f)
        );
        cubicTo(
            interpolate(glyphProgress, 65.0f, 55.0f),
            interpolate(glyphProgress, 126f, 4f),
            interpolate(glyphProgress, 55.0f, 67.0f),
            interpolate(glyphProgress, 130f, 14f),
            interpolate(glyphProgress, 43.0f, 72.0f),
            interpolate(glyphProgress, 130f, 26f)
        );
        cubicTo(
            interpolate(glyphProgress, 32.0f, 78.0f),
            interpolate(glyphProgress, 130f, 41f),
            interpolate(glyphProgress, 22.0f, 69.0f),
            interpolate(glyphProgress, 126f, 57f),
            interpolate(glyphProgress, 14.0f, 65.0f),
            interpolate(glyphProgress, 119f, 63f)
        );
        cubicTo(
            interpolate(glyphProgress, 6.0f, 62.0f),
            interpolate(glyphProgress, 112f, 70f),
            interpolate(glyphProgress, 2.0f, 33.0f),
            interpolate(glyphProgress, 104f, 112f),
            interpolate(glyphProgress, 2.0f, 33.0f),
            interpolate(glyphProgress, 93f, 112f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 33.0f),
            interpolate(glyphProgress, 90f, 112f),
            interpolate(glyphProgress, 3.0f, 44.0f),
            interpolate(glyphProgress, 86f, 123f),
            interpolate(glyphProgress, 4.0f, 44.0f),
            interpolate(glyphProgress, 83f, 123f)
        );
        cubicTo(
            interpolate(glyphProgress, 4.0f, 44.0f),
            interpolate(glyphProgress, 83f, 123f),
            interpolate(glyphProgress, 4.0f, 44.0f),
            interpolate(glyphProgress, 83f, 123f),
            interpolate(glyphProgress, 4.0f, 44.0f),
            interpolate(glyphProgress, 83f, 123f)
        );
    }

    override fun GenericCanvas.drawNineZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
        moveTo(
            interpolate(glyphProgress, 44.0f, 66.0f),
            interpolate(glyphProgress, 123f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 44.0f, 66.0f),
            interpolate(glyphProgress, 123f, 130f),
            interpolate(glyphProgress, 51.0f, 66.0f),
            interpolate(glyphProgress, 130f, 130f),
            interpolate(glyphProgress, 51.0f, 66.0f),
            interpolate(glyphProgress, 130f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 51.0f, 66.0f),
            interpolate(glyphProgress, 130f, 130f),
            interpolate(glyphProgress, 79.0f, 66.0f),
            interpolate(glyphProgress, 89f, 130f),
            interpolate(glyphProgress, 83.0f, 66.0f),
            interpolate(glyphProgress, 82f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 86.0f, 66.0f),
            interpolate(glyphProgress, 75f, 130f),
            interpolate(glyphProgress, 95.0f, 66.0f),
            interpolate(glyphProgress, 59f, 130f),
            interpolate(glyphProgress, 89.0f, 66.0f),
            interpolate(glyphProgress, 44f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 84.0f, 66.0f),
            interpolate(glyphProgress, 30f, 130f),
            interpolate(glyphProgress, 69.0f, 66.0f),
            interpolate(glyphProgress, 20f, 130f),
            interpolate(glyphProgress, 52.0f, 66.0f),
            interpolate(glyphProgress, 21f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 51.0f, 66.0f),
            interpolate(glyphProgress, 21f, 130f),
            interpolate(glyphProgress, 50.0f, 66.0f),
            interpolate(glyphProgress, 21f, 130f),
            interpolate(glyphProgress, 49.0f, 66.0f),
            interpolate(glyphProgress, 21f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 30.0f, 66.0f),
            interpolate(glyphProgress, 25f, 130f),
            interpolate(glyphProgress, 20.0f, 66.0f),
            interpolate(glyphProgress, 44f, 130f),
            interpolate(glyphProgress, 20.0f, 66.0f),
            interpolate(glyphProgress, 54f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 19.0f, 66.0f),
            interpolate(glyphProgress, 65f, 130f),
            interpolate(glyphProgress, 23.0f, 66.0f),
            interpolate(glyphProgress, 79f, 130f),
            interpolate(glyphProgress, 38.0f, 66.0f),
            interpolate(glyphProgress, 86f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 50.0f, 66.0f),
            interpolate(glyphProgress, 92f, 130f),
            interpolate(glyphProgress, 64.0f, 66.0f),
            interpolate(glyphProgress, 88f, 130f),
            interpolate(glyphProgress, 64.0f, 66.0f),
            interpolate(glyphProgress, 88f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 64.0f, 66.0f),
            interpolate(glyphProgress, 88f, 130f),
            interpolate(glyphProgress, 47.0f, 66.0f),
            interpolate(glyphProgress, 69f, 130f),
            interpolate(glyphProgress, 47.0f, 66.0f),
            interpolate(glyphProgress, 69f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 47.0f, 66.0f),
            interpolate(glyphProgress, 69f, 130f),
            interpolate(glyphProgress, 43.0f, 66.0f),
            interpolate(glyphProgress, 70f, 130f),
            interpolate(glyphProgress, 42.0f, 66.0f),
            interpolate(glyphProgress, 70f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 42.0f, 66.0f),
            interpolate(glyphProgress, 70f, 130f),
            interpolate(glyphProgress, 43.0f, 66.0f),
            interpolate(glyphProgress, 70f, 130f),
            interpolate(glyphProgress, 43.0f, 66.0f),
            interpolate(glyphProgress, 70f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 38.0f, 66.0f),
            interpolate(glyphProgress, 71f, 130f),
            interpolate(glyphProgress, 30.0f, 66.0f),
            interpolate(glyphProgress, 72f, 130f),
            interpolate(glyphProgress, 20.0f, 66.0f),
            interpolate(glyphProgress, 68f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 6.0f, 66.0f),
            interpolate(glyphProgress, 61f, 130f),
            interpolate(glyphProgress, 2.0f, 66.0f),
            interpolate(glyphProgress, 46f, 130f),
            interpolate(glyphProgress, 2.0f, 66.0f),
            interpolate(glyphProgress, 36f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 3.0f, 66.0f),
            interpolate(glyphProgress, 26f, 130f),
            interpolate(glyphProgress, 12.0f, 66.0f),
            interpolate(glyphProgress, 7f, 130f),
            interpolate(glyphProgress, 31.0f, 66.0f),
            interpolate(glyphProgress, 3f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 35.0f, 66.0f),
            interpolate(glyphProgress, 2f, 130f),
            interpolate(glyphProgress, 38.0f, 66.0f),
            interpolate(glyphProgress, 2f, 130f),
            interpolate(glyphProgress, 42.0f, 66.0f),
            interpolate(glyphProgress, 2f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 55.0f, 101.0f),
            interpolate(glyphProgress, 4f, 130f),
            interpolate(glyphProgress, 67.0f, 130.0f),
            interpolate(glyphProgress, 14f, 101f),
            interpolate(glyphProgress, 72.0f, 130.0f),
            interpolate(glyphProgress, 26f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 78.0f, 130.0f),
            interpolate(glyphProgress, 41f, 31f),
            interpolate(glyphProgress, 69.0f, 101.0f),
            interpolate(glyphProgress, 57f, 2f),
            interpolate(glyphProgress, 65.0f, 66.0f),
            interpolate(glyphProgress, 63f, 2f)
        );
        cubicTo(
            interpolate(glyphProgress, 62.0f, 31.0f),
            interpolate(glyphProgress, 70f, 2f),
            interpolate(glyphProgress, 33.0f, 2.0f),
            interpolate(glyphProgress, 112f, 31f),
            interpolate(glyphProgress, 33.0f, 2.0f),
            interpolate(glyphProgress, 112f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 33.0f, 2.0f),
            interpolate(glyphProgress, 112f, 101f),
            interpolate(glyphProgress, 44.0f, 31.0f),
            interpolate(glyphProgress, 123f, 130f),
            interpolate(glyphProgress, 44.0f, 66.0f),
            interpolate(glyphProgress, 123f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 44.0f, 66.0f),
            interpolate(glyphProgress, 123f, 130f),
            interpolate(glyphProgress, 44.0f, 66.0f),
            interpolate(glyphProgress, 123f, 130f),
            interpolate(glyphProgress, 44.0f, 66.0f),
            interpolate(glyphProgress, 123f, 130f)
        );
    }

    override fun GenericCanvas.drawOneZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawZeroOne(1f - glyphProgress, paints)
    }

    override fun GenericCanvas.drawTwoZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
        moveTo(
            interpolate(glyphProgress, 87.0f, 66.0f),
            interpolate(glyphProgress, 130f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 87.0f, 66.0f),
            interpolate(glyphProgress, 130f, 130f),
            interpolate(glyphProgress, 20.0f, 66.0f),
            interpolate(glyphProgress, 130f, 130f),
            interpolate(glyphProgress, 20.0f, 66.0f),
            interpolate(glyphProgress, 130f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 20.0f, 66.0f),
            interpolate(glyphProgress, 130f, 130f),
            interpolate(glyphProgress, 30.0f, 66.0f),
            interpolate(glyphProgress, 122f, 130f),
            interpolate(glyphProgress, 36.0f, 66.0f),
            interpolate(glyphProgress, 117f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 42.0f, 66.0f),
            interpolate(glyphProgress, 111f, 130f),
            interpolate(glyphProgress, 49.0f, 66.0f),
            interpolate(glyphProgress, 105f, 130f),
            interpolate(glyphProgress, 67.0f, 66.0f),
            interpolate(glyphProgress, 88f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 71.0f, 66.0f),
            interpolate(glyphProgress, 84f, 130f),
            interpolate(glyphProgress, 74.0f, 66.0f),
            interpolate(glyphProgress, 80f, 130f),
            interpolate(glyphProgress, 77.0f, 66.0f),
            interpolate(glyphProgress, 77f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 85.0f, 66.0f),
            interpolate(glyphProgress, 66f, 130f),
            interpolate(glyphProgress, 86.0f, 66.0f),
            interpolate(glyphProgress, 59f, 130f),
            interpolate(glyphProgress, 86.0f, 66.0f),
            interpolate(glyphProgress, 49f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 87.0f, 66.0f),
            interpolate(glyphProgress, 36f, 130f),
            interpolate(glyphProgress, 73.0f, 66.0f),
            interpolate(glyphProgress, 19f, 130f),
            interpolate(glyphProgress, 50.0f, 66.0f),
            interpolate(glyphProgress, 21f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 27.0f, 66.0f),
            interpolate(glyphProgress, 22f, 130f),
            interpolate(glyphProgress, 20.0f, 66.0f),
            interpolate(glyphProgress, 44f, 130f),
            interpolate(glyphProgress, 20.0f, 66.0f),
            interpolate(glyphProgress, 44f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 20.0f, 66.0f),
            interpolate(glyphProgress, 44f, 130f),
            interpolate(glyphProgress, 2.0f, 66.0f),
            interpolate(glyphProgress, 25f, 130f),
            interpolate(glyphProgress, 2.0f, 66.0f),
            interpolate(glyphProgress, 25f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 66.0f),
            interpolate(glyphProgress, 25f, 130f),
            interpolate(glyphProgress, 9.0f, 66.0f),
            interpolate(glyphProgress, 4f, 130f),
            interpolate(glyphProgress, 32.0f, 66.0f),
            interpolate(glyphProgress, 2f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 55.0f, 66.0f),
            interpolate(glyphProgress, 0f, 130f),
            interpolate(glyphProgress, 69.0f, 66.0f),
            interpolate(glyphProgress, 18f, 130f),
            interpolate(glyphProgress, 68.0f, 66.0f),
            interpolate(glyphProgress, 30f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 68.0f, 66.0f),
            interpolate(glyphProgress, 43f, 130f),
            interpolate(glyphProgress, 67.0f, 66.0f),
            interpolate(glyphProgress, 51f, 130f),
            interpolate(glyphProgress, 49.0f, 66.0f),
            interpolate(glyphProgress, 69f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 47.0f, 66.0f),
            interpolate(glyphProgress, 71f, 130f),
            interpolate(glyphProgress, 44.0f, 66.0f),
            interpolate(glyphProgress, 74f, 130f),
            interpolate(glyphProgress, 42.0f, 66.0f),
            interpolate(glyphProgress, 76f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 29.0f, 101.0f),
            interpolate(glyphProgress, 88f, 130f),
            interpolate(glyphProgress, 22.0f, 130.0f),
            interpolate(glyphProgress, 94f, 101f),
            interpolate(glyphProgress, 17.0f, 130.0f),
            interpolate(glyphProgress, 99f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 11.0f, 130.0f),
            interpolate(glyphProgress, 103f, 31f),
            interpolate(glyphProgress, 2.0f, 101.0f),
            interpolate(glyphProgress, 111f, 2f),
            interpolate(glyphProgress, 2.0f, 66.0f),
            interpolate(glyphProgress, 111f, 2f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 31.0f),
            interpolate(glyphProgress, 111f, 2f),
            interpolate(glyphProgress, 70.0f, 2.0f),
            interpolate(glyphProgress, 111f, 31f),
            interpolate(glyphProgress, 70.0f, 2.0f),
            interpolate(glyphProgress, 111f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 70.0f, 2.0f),
            interpolate(glyphProgress, 111f, 101f),
            interpolate(glyphProgress, 87.0f, 31.0f),
            interpolate(glyphProgress, 130f, 130f),
            interpolate(glyphProgress, 87.0f, 66.0f),
            interpolate(glyphProgress, 130f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 87.0f, 66.0f),
            interpolate(glyphProgress, 130f, 130f),
            interpolate(glyphProgress, 87.0f, 66.0f),
            interpolate(glyphProgress, 130f, 130f),
            interpolate(glyphProgress, 87.0f, 66.0f),
            interpolate(glyphProgress, 130f, 130f)
        );
    }

    override fun GenericCanvas.drawThreeZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
        moveTo(
            interpolate(glyphProgress, 20.0f, 66.0f),
            interpolate(glyphProgress, 104f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 20.0f, 66.0f),
            interpolate(glyphProgress, 104f, 130f),
            interpolate(glyphProgress, 23.0f, 66.0f),
            interpolate(glyphProgress, 113f, 130f),
            interpolate(glyphProgress, 31.0f, 66.0f),
            interpolate(glyphProgress, 122f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 40.0f, 66.0f),
            interpolate(glyphProgress, 130f, 130f),
            interpolate(glyphProgress, 59.0f, 66.0f),
            interpolate(glyphProgress, 134f, 130f),
            interpolate(glyphProgress, 73.0f, 66.0f),
            interpolate(glyphProgress, 125f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 86.0f, 66.0f),
            interpolate(glyphProgress, 117f, 130f),
            interpolate(glyphProgress, 91.0f, 66.0f),
            interpolate(glyphProgress, 100f, 130f),
            interpolate(glyphProgress, 88.0f, 66.0f),
            interpolate(glyphProgress, 89f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 84.0f, 66.0f),
            interpolate(glyphProgress, 74f, 130f),
            interpolate(glyphProgress, 71.0f, 66.0f),
            interpolate(glyphProgress, 72f, 130f),
            interpolate(glyphProgress, 71.0f, 66.0f),
            interpolate(glyphProgress, 72f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 71.0f, 66.0f),
            interpolate(glyphProgress, 72f, 130f),
            interpolate(glyphProgress, 73.0f, 66.0f),
            interpolate(glyphProgress, 71f, 130f),
            interpolate(glyphProgress, 74.0f, 66.0f),
            interpolate(glyphProgress, 70f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 78.0f, 66.0f),
            interpolate(glyphProgress, 67f, 130f),
            interpolate(glyphProgress, 85.0f, 66.0f),
            interpolate(glyphProgress, 60f, 130f),
            interpolate(glyphProgress, 85.0f, 66.0f),
            interpolate(glyphProgress, 50f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 85.0f, 66.0f),
            interpolate(glyphProgress, 35f, 130f),
            interpolate(glyphProgress, 74.0f, 66.0f),
            interpolate(glyphProgress, 27f, 130f),
            interpolate(glyphProgress, 69.0f, 66.0f),
            interpolate(glyphProgress, 24f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 65.0f, 66.0f),
            interpolate(glyphProgress, 22f, 130f),
            interpolate(glyphProgress, 56.0f, 66.0f),
            interpolate(glyphProgress, 19f, 130f),
            interpolate(glyphProgress, 42.0f, 66.0f),
            interpolate(glyphProgress, 22f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 28.0f, 66.0f),
            interpolate(glyphProgress, 26f, 130f),
            interpolate(glyphProgress, 22.0f, 66.0f),
            interpolate(glyphProgress, 42f, 130f),
            interpolate(glyphProgress, 22.0f, 66.0f),
            interpolate(glyphProgress, 42f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 22.0f, 66.0f),
            interpolate(glyphProgress, 42f, 130f),
            interpolate(glyphProgress, 4.0f, 66.0f),
            interpolate(glyphProgress, 24f, 130f),
            interpolate(glyphProgress, 4.0f, 66.0f),
            interpolate(glyphProgress, 24f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 4.0f, 66.0f),
            interpolate(glyphProgress, 24f, 130f),
            interpolate(glyphProgress, 10.0f, 66.0f),
            interpolate(glyphProgress, 8f, 130f),
            interpolate(glyphProgress, 25.0f, 66.0f),
            interpolate(glyphProgress, 4f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 38.0f, 66.0f),
            interpolate(glyphProgress, 0f, 130f),
            interpolate(glyphProgress, 48.0f, 66.0f),
            interpolate(glyphProgress, 4f, 130f),
            interpolate(glyphProgress, 53.0f, 66.0f),
            interpolate(glyphProgress, 6f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 57.0f, 66.0f),
            interpolate(glyphProgress, 9f, 130f),
            interpolate(glyphProgress, 67.0f, 66.0f),
            interpolate(glyphProgress, 17f, 130f),
            interpolate(glyphProgress, 67.0f, 66.0f),
            interpolate(glyphProgress, 32f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 68.0f, 66.0f),
            interpolate(glyphProgress, 47f, 130f),
            interpolate(glyphProgress, 54.0f, 66.0f),
            interpolate(glyphProgress, 53f, 130f),
            interpolate(glyphProgress, 54.0f, 66.0f),
            interpolate(glyphProgress, 53f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 54.0f, 66.0f),
            interpolate(glyphProgress, 53f, 130f),
            interpolate(glyphProgress, 58.0f, 66.0f),
            interpolate(glyphProgress, 56f, 130f),
            interpolate(glyphProgress, 63.0f, 66.0f),
            interpolate(glyphProgress, 60f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 66.0f, 66.0f),
            interpolate(glyphProgress, 64f, 130f),
            interpolate(glyphProgress, 71.0f, 66.0f),
            interpolate(glyphProgress, 70f, 130f),
            interpolate(glyphProgress, 71.0f, 66.0f),
            interpolate(glyphProgress, 75f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 71.0f, 101.0f),
            interpolate(glyphProgress, 87f, 130f),
            interpolate(glyphProgress, 68.0f, 130.0f),
            interpolate(glyphProgress, 98f, 101f),
            interpolate(glyphProgress, 55.0f, 130.0f),
            interpolate(glyphProgress, 106f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 42.0f, 130.0f),
            interpolate(glyphProgress, 115f, 31f),
            interpolate(glyphProgress, 21.0f, 101.0f),
            interpolate(glyphProgress, 111f, 2f),
            interpolate(glyphProgress, 13.0f, 66.0f),
            interpolate(glyphProgress, 103f, 2f)
        );
        cubicTo(
            interpolate(glyphProgress, 5.0f, 31.0f),
            interpolate(glyphProgress, 95f, 2f),
            interpolate(glyphProgress, 2.0f, 2.0f),
            interpolate(glyphProgress, 85f, 31f),
            interpolate(glyphProgress, 2.0f, 2.0f),
            interpolate(glyphProgress, 85f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 2.0f),
            interpolate(glyphProgress, 85f, 101f),
            interpolate(glyphProgress, 20.0f, 31.0f),
            interpolate(glyphProgress, 104f, 130f),
            interpolate(glyphProgress, 20.0f, 66.0f),
            interpolate(glyphProgress, 104f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 20.0f, 66.0f),
            interpolate(glyphProgress, 104f, 130f),
            interpolate(glyphProgress, 20.0f, 66.0f),
            interpolate(glyphProgress, 104f, 130f),
            interpolate(glyphProgress, 20.0f, 66.0f),
            interpolate(glyphProgress, 104f, 130f)
        );
    }

    override fun GenericCanvas.drawFiveZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
        moveTo(
            interpolate(glyphProgress, 2.0f, 66.0f),
            interpolate(glyphProgress, 82f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 66.0f),
            interpolate(glyphProgress, 82f, 130f),
            interpolate(glyphProgress, 20.0f, 66.0f),
            interpolate(glyphProgress, 101f, 130f),
            interpolate(glyphProgress, 20.0f, 66.0f),
            interpolate(glyphProgress, 101f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 20.0f, 66.0f),
            interpolate(glyphProgress, 101f, 130f),
            interpolate(glyphProgress, 24.0f, 66.0f),
            interpolate(glyphProgress, 111f, 130f),
            interpolate(glyphProgress, 25.0f, 66.0f),
            interpolate(glyphProgress, 113f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 25.0f, 66.0f),
            interpolate(glyphProgress, 114f, 130f),
            interpolate(glyphProgress, 35.0f, 66.0f),
            interpolate(glyphProgress, 130f, 130f),
            interpolate(glyphProgress, 56.0f, 66.0f),
            interpolate(glyphProgress, 130f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 77.0f, 66.0f),
            interpolate(glyphProgress, 130f, 130f),
            interpolate(glyphProgress, 93.0f, 66.0f),
            interpolate(glyphProgress, 114f, 130f),
            interpolate(glyphProgress, 93.0f, 66.0f),
            interpolate(glyphProgress, 94f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 93.0f, 66.0f),
            interpolate(glyphProgress, 75f, 130f),
            interpolate(glyphProgress, 76.0f, 66.0f),
            interpolate(glyphProgress, 59f, 130f),
            interpolate(glyphProgress, 57.0f, 66.0f),
            interpolate(glyphProgress, 59f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 56.0f, 66.0f),
            interpolate(glyphProgress, 59f, 130f),
            interpolate(glyphProgress, 54.0f, 66.0f),
            interpolate(glyphProgress, 59f, 130f),
            interpolate(glyphProgress, 53.0f, 66.0f),
            interpolate(glyphProgress, 59f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 36.0f, 66.0f),
            interpolate(glyphProgress, 61f, 130f),
            interpolate(glyphProgress, 23.0f, 66.0f),
            interpolate(glyphProgress, 75f, 130f),
            interpolate(glyphProgress, 23.0f, 66.0f),
            interpolate(glyphProgress, 75f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 23.0f, 66.0f),
            interpolate(glyphProgress, 75f, 130f),
            interpolate(glyphProgress, 30.0f, 66.0f),
            interpolate(glyphProgress, 21f, 130f),
            interpolate(glyphProgress, 30.0f, 66.0f),
            interpolate(glyphProgress, 21f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 30.0f, 66.0f),
            interpolate(glyphProgress, 21f, 130f),
            interpolate(glyphProgress, 86.0f, 66.0f),
            interpolate(glyphProgress, 21f, 130f),
            interpolate(glyphProgress, 86.0f, 66.0f),
            interpolate(glyphProgress, 21f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 86.0f, 66.0f),
            interpolate(glyphProgress, 21f, 130f),
            interpolate(glyphProgress, 68.0f, 66.0f),
            interpolate(glyphProgress, 2f, 130f),
            interpolate(glyphProgress, 68.0f, 66.0f),
            interpolate(glyphProgress, 2f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 68.0f, 66.0f),
            interpolate(glyphProgress, 2f, 130f),
            interpolate(glyphProgress, 12.0f, 66.0f),
            interpolate(glyphProgress, 2f, 130f),
            interpolate(glyphProgress, 12.0f, 66.0f),
            interpolate(glyphProgress, 2f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 12.0f, 66.0f),
            interpolate(glyphProgress, 2f, 130f),
            interpolate(glyphProgress, 5.0f, 66.0f),
            interpolate(glyphProgress, 56f, 130f),
            interpolate(glyphProgress, 5.0f, 66.0f),
            interpolate(glyphProgress, 56f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 5.0f, 66.0f),
            interpolate(glyphProgress, 56f, 130f),
            interpolate(glyphProgress, 20.0f, 66.0f),
            interpolate(glyphProgress, 43f, 130f),
            interpolate(glyphProgress, 36.0f, 66.0f),
            interpolate(glyphProgress, 40f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 37.0f, 66.0f),
            interpolate(glyphProgress, 40f, 130f),
            interpolate(glyphProgress, 38.0f, 66.0f),
            interpolate(glyphProgress, 40f, 130f),
            interpolate(glyphProgress, 39.0f, 66.0f),
            interpolate(glyphProgress, 40f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 58.0f, 101.0f),
            interpolate(glyphProgress, 40f, 130f),
            interpolate(glyphProgress, 75.0f, 130.0f),
            interpolate(glyphProgress, 56f, 101f),
            interpolate(glyphProgress, 75.0f, 130.0f),
            interpolate(glyphProgress, 75f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 75.0f, 130.0f),
            interpolate(glyphProgress, 95f, 31f),
            interpolate(glyphProgress, 59.0f, 101.0f),
            interpolate(glyphProgress, 111f, 2f),
            interpolate(glyphProgress, 38.0f, 66.0f),
            interpolate(glyphProgress, 111f, 2f)
        );
        cubicTo(
            interpolate(glyphProgress, 17.0f, 31.0f),
            interpolate(glyphProgress, 111f, 2f),
            interpolate(glyphProgress, 8.0f, 2.0f),
            interpolate(glyphProgress, 95f, 31f),
            interpolate(glyphProgress, 7.0f, 2.0f),
            interpolate(glyphProgress, 94f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 6.0f, 2.0f),
            interpolate(glyphProgress, 92f, 101f),
            interpolate(glyphProgress, 2.0f, 31.0f),
            interpolate(glyphProgress, 82f, 130f),
            interpolate(glyphProgress, 2.0f, 66.0f),
            interpolate(glyphProgress, 82f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 66.0f),
            interpolate(glyphProgress, 82f, 130f),
            interpolate(glyphProgress, 2.0f, 66.0f),
            interpolate(glyphProgress, 82f, 130f),
            interpolate(glyphProgress, 2.0f, 66.0f),
            interpolate(glyphProgress, 82f, 130f)
        );
    }

    override fun GenericCanvas.drawOneEmpty(
        glyphProgress: Float,
        paints: Paints,
    ) {
        moveTo(
            interpolate(glyphProgress, 2.0f, 0f),
            interpolate(glyphProgress, 2f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 0f),
            interpolate(glyphProgress, 2f, 66f),
            interpolate(glyphProgress, 33.0f, 0f),
            interpolate(glyphProgress, 2f, 66f),
            interpolate(glyphProgress, 33.0f, 0f),
            interpolate(glyphProgress, 2f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 33.0f, 0f),
            interpolate(glyphProgress, 2f, 66f),
            interpolate(glyphProgress, 33.0f, 0f),
            interpolate(glyphProgress, 130f, 66f),
            interpolate(glyphProgress, 33.0f, 0f),
            interpolate(glyphProgress, 130f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 33.0f, 0f),
            interpolate(glyphProgress, 130f, 66f),
            interpolate(glyphProgress, 2.0f, 0f),
            interpolate(glyphProgress, 130f, 66f),
            interpolate(glyphProgress, 2.0f, 0f),
            interpolate(glyphProgress, 130f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 0f),
            interpolate(glyphProgress, 130f, 66f),
            interpolate(glyphProgress, 2.0f, 0f),
            interpolate(glyphProgress, 2f, 66f),
            interpolate(glyphProgress, 2.0f, 0f),
            interpolate(glyphProgress, 2f, 66f)
        );
    }

    override fun GenericCanvas.drawTwoEmpty(
        glyphProgress: Float,
        paints: Paints,
    ) {
        moveTo(
            interpolate(glyphProgress, 87.0f, 0f),
            interpolate(glyphProgress, 130f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 87.0f, 0f),
            interpolate(glyphProgress, 130f, 66f),
            interpolate(glyphProgress, 20.0f, 0f),
            interpolate(glyphProgress, 130f, 66f),
            interpolate(glyphProgress, 20.0f, 0f),
            interpolate(glyphProgress, 130f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 20.0f, 0f),
            interpolate(glyphProgress, 130f, 66f),
            interpolate(glyphProgress, 30.0f, 0f),
            interpolate(glyphProgress, 122f, 66f),
            interpolate(glyphProgress, 36.0f, 0f),
            interpolate(glyphProgress, 117f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 42.0f, 0f),
            interpolate(glyphProgress, 111f, 66f),
            interpolate(glyphProgress, 49.0f, 0f),
            interpolate(glyphProgress, 105f, 66f),
            interpolate(glyphProgress, 67.0f, 0f),
            interpolate(glyphProgress, 88f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 71.0f, 0f),
            interpolate(glyphProgress, 84f, 66f),
            interpolate(glyphProgress, 74.0f, 0f),
            interpolate(glyphProgress, 80f, 66f),
            interpolate(glyphProgress, 77.0f, 0f),
            interpolate(glyphProgress, 77f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 85.0f, 0f),
            interpolate(glyphProgress, 66f, 66f),
            interpolate(glyphProgress, 86.0f, 0f),
            interpolate(glyphProgress, 59f, 66f),
            interpolate(glyphProgress, 86.0f, 0f),
            interpolate(glyphProgress, 49f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 87.0f, 0f),
            interpolate(glyphProgress, 36f, 66f),
            interpolate(glyphProgress, 73.0f, 0f),
            interpolate(glyphProgress, 19f, 66f),
            interpolate(glyphProgress, 50.0f, 0f),
            interpolate(glyphProgress, 21f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 27.0f, 0f),
            interpolate(glyphProgress, 22f, 66f),
            interpolate(glyphProgress, 20.0f, 0f),
            interpolate(glyphProgress, 44f, 66f),
            interpolate(glyphProgress, 20.0f, 0f),
            interpolate(glyphProgress, 44f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 20.0f, 0f),
            interpolate(glyphProgress, 44f, 66f),
            interpolate(glyphProgress, 2.0f, 0f),
            interpolate(glyphProgress, 25f, 66f),
            interpolate(glyphProgress, 2.0f, 0f),
            interpolate(glyphProgress, 25f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 0f),
            interpolate(glyphProgress, 25f, 66f),
            interpolate(glyphProgress, 9.0f, 0f),
            interpolate(glyphProgress, 4f, 66f),
            interpolate(glyphProgress, 32.0f, 0f),
            interpolate(glyphProgress, 2f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 55.0f, 0f),
            interpolate(glyphProgress, 0f, 66f),
            interpolate(glyphProgress, 69.0f, 0f),
            interpolate(glyphProgress, 18f, 66f),
            interpolate(glyphProgress, 68.0f, 0f),
            interpolate(glyphProgress, 30f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 68.0f, 0f),
            interpolate(glyphProgress, 43f, 66f),
            interpolate(glyphProgress, 67.0f, 0f),
            interpolate(glyphProgress, 51f, 66f),
            interpolate(glyphProgress, 49.0f, 0f),
            interpolate(glyphProgress, 69f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 47.0f, 0f),
            interpolate(glyphProgress, 71f, 66f),
            interpolate(glyphProgress, 44.0f, 0f),
            interpolate(glyphProgress, 74f, 66f),
            interpolate(glyphProgress, 42.0f, 0f),
            interpolate(glyphProgress, 76f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 29.0f, 0f),
            interpolate(glyphProgress, 88f, 66f),
            interpolate(glyphProgress, 22.0f, 0f),
            interpolate(glyphProgress, 94f, 66f),
            interpolate(glyphProgress, 17.0f, 0f),
            interpolate(glyphProgress, 99f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 11.0f, 0f),
            interpolate(glyphProgress, 103f, 66f),
            interpolate(glyphProgress, 2.0f, 0f),
            interpolate(glyphProgress, 111f, 66f),
            interpolate(glyphProgress, 2.0f, 0f),
            interpolate(glyphProgress, 111f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 2.0f, 0f),
            interpolate(glyphProgress, 111f, 66f),
            interpolate(glyphProgress, 70.0f, 0f),
            interpolate(glyphProgress, 111f, 66f),
            interpolate(glyphProgress, 70.0f, 0f),
            interpolate(glyphProgress, 111f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 70.0f, 0f),
            interpolate(glyphProgress, 111f, 66f),
            interpolate(glyphProgress, 87.0f, 0f),
            interpolate(glyphProgress, 130f, 66f),
            interpolate(glyphProgress, 87.0f, 0f),
            interpolate(glyphProgress, 130f, 66f)
        );
        cubicTo(
            interpolate(glyphProgress, 87.0f, 0f),
            interpolate(glyphProgress, 130f, 66f),
            interpolate(glyphProgress, 87.0f, 0f),
            interpolate(glyphProgress, 130f, 66f),
            interpolate(glyphProgress, 87.0f, 0f),
            interpolate(glyphProgress, 130f, 66f)
        );
    }

    override fun GenericCanvas.drawEmptyOne(
        glyphProgress: Float,
        paints: Paints,
    ) {
        moveTo(
            interpolate(glyphProgress, 0f, 2.0f),
            interpolate(glyphProgress, 66f, 2f)
        );
        cubicTo(
            interpolate(glyphProgress, 0f, 2.0f),
            interpolate(glyphProgress, 66f, 2f),
            interpolate(glyphProgress, 0f, 33.0f),
            interpolate(glyphProgress, 66f, 2f),
            interpolate(glyphProgress, 0f, 33.0f),
            interpolate(glyphProgress, 66f, 2f)
        );
        cubicTo(
            interpolate(glyphProgress, 0f, 33.0f),
            interpolate(glyphProgress, 66f, 2f),
            interpolate(glyphProgress, 0f, 33.0f),
            interpolate(glyphProgress, 66f, 130f),
            interpolate(glyphProgress, 0f, 33.0f),
            interpolate(glyphProgress, 66f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 0f, 33.0f),
            interpolate(glyphProgress, 66f, 130f),
            interpolate(glyphProgress, 0f, 2.0f),
            interpolate(glyphProgress, 66f, 130f),
            interpolate(glyphProgress, 0f, 2.0f),
            interpolate(glyphProgress, 66f, 130f)
        );
        cubicTo(
            interpolate(glyphProgress, 0f, 2.0f),
            interpolate(glyphProgress, 66f, 130f),
            interpolate(glyphProgress, 0f, 2.0f),
            interpolate(glyphProgress, 66f, 2f),
            interpolate(glyphProgress, 0f, 2.0f),
            interpolate(glyphProgress, 66f, 2f)
        );
    }

    override fun GenericCanvas.drawSeparator(
        glyphProgress: Float,
        paints: Paints,
    ) {
        circle(8f, 48f, 6f)
        circle(8f, 96f, 6f)
    }

    override fun GenericCanvas.drawSpace(
        glyphProgress: Float,
        paints: Paints,
    ) {
        // Page intentionally blank
    }

    override fun GenericCanvas.drawHash(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawNotImplemented(glyphProgress, paints)
    }

    override fun getWidthAtProgress(glyphProgress: Float): Float {
        val p = ease(glyphProgress)
        return when (key) {
            "0", "0_1" -> interpolate(p, Width0, Width1)
            "1", "1_2" -> interpolate(p, Width1, Width2)
            "2", "2_3" -> interpolate(p, Width2, Width3)
            "3", "3_4" -> interpolate(p, Width3, Width4)
            "4", "4_5" -> interpolate(p, Width4, Width5)
            "5", "5_6" -> interpolate(p, Width5, Width6)
            "6", "6_7" -> interpolate(p, Width6, Width7)
            "7", "7_8" -> interpolate(p, Width7, Width8)
            "8", "8_9" -> interpolate(p, Width8, Width9)
            "9", "9_0" -> interpolate(p, Width9, Width0)
            " _1" -> interpolate(p, 0f, Width1)
            "1_ " -> interpolate(p, Width1, 0f)
            "2_ " -> interpolate(p, Width2, 0f)
            "2_1" -> interpolate(p, Width2, Width1)
            "3_0" -> interpolate(p, Width3, Width0)
            "5_0" -> interpolate(p, Width5, Width0)
            "2_0" -> interpolate(p, Width2, Width0)
            ":" -> WidthSeparator
            "#" -> 49f
            " ", "_" -> 0f

            else -> throw IllegalArgumentException("getWidthAtProgress unhandled key $key")
        }
    }
}
