package org.beatonma.gclocks.core.graphics

import org.beatonma.gclocks.test.shouldbe
import kotlin.test.Test

class ColorTest {
    @Test
    fun `color components with opacity are correct`() {
        with(Color(0xffffffffL)) {
            alpha shouldbe 255
            red shouldbe 255
            green shouldbe 255
            blue shouldbe 255
        }

        with(Color(0x7f7f7f7fL)) {
            alpha shouldbe 127
            red shouldbe 127
            green shouldbe 127
            blue shouldbe 127
        }

        with(Color(0x00000000L)) {
            alpha shouldbe 0
            red shouldbe 0
            green shouldbe 0
            blue shouldbe 0
        }
    }

    @Test
    fun `color components without opacity are correct`() {
        with(Color(0x000000)) {
            alpha shouldbe 255
            red shouldbe 0
            green shouldbe 0
            blue shouldbe 0
        }
        with(Color(0xabcdef)) {
            alpha shouldbe 255
            red shouldbe 0xab
            green shouldbe 0xcd
            blue shouldbe 0xef
        }
        with(Color(0x000000)) {
            alpha shouldbe 255
            red shouldbe 0
            green shouldbe 0
            blue shouldbe 0
        }
    }

    @Test
    fun `withOpacity is correct`() {
        with(Color.Red.withOpacity(1f)) {
            red shouldbe 255
            alpha shouldbe 255
        }
        with(Color.Red.withOpacity(0.5f)) {
            red shouldbe 255
            alpha shouldbe 127
        }
        with(Color.Red.withOpacity(0f)) {
            red shouldbe 255
            alpha shouldbe 0
        }

        with(Color.Red.withOpacity(0.5f).withOpacity(1f)) {
            red shouldbe 255
            alpha shouldbe 255
        }
    }

    @Test
    fun `toStringRgb is correct`() {
        Color.Red.toStringRgb() shouldbe "ff0000"
        Color.Green.toStringRgb() shouldbe "00ff00"
        Color.Blue.toStringRgb() shouldbe "0000ff"

        Color.Red.withOpacity(0.5f).toStringRgb() shouldbe "ff0000"
    }

    @Test
    fun `toStringArgb is correct`() {
        Color.Red.toStringArgb() shouldbe "ffff0000"
        Color.Green.toStringArgb() shouldbe "ff00ff00"
        Color.Blue.toStringArgb() shouldbe "ff0000ff"

        Color.Red.withOpacity(0.5f).toStringArgb() shouldbe "7fff0000"
    }

    @Test
    fun `toRgbInt is correct`() {
        Color.Red.toRgbInt() shouldbe 0xffff0000.toInt()
        Color.Green.toRgbInt() shouldbe 0xff00ff00.toInt()
        Color.Blue.toRgbInt() shouldbe 0xff0000ff.toInt()

        Color.Red.withOpacity(0.5f).toRgbInt() shouldbe 0xffff0000.toInt()
    }

    @Test
    fun `toArgbInt is correct`() {
        Color.Red.toArgbInt() shouldbe 0xffff0000.toInt()
        Color.Green.toArgbInt() shouldbe 0xff00ff00.toInt()
        Color.Blue.toArgbInt() shouldbe 0xff0000ff.toInt()

        Color.Red.withOpacity(0.5f).toArgbInt() shouldbe 0x7fff0000
    }
}
