package org.beatonma.gclocks.app.data

import org.beatonma.gclocks.app.data.settings.ClockType
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.test.shouldbe
import kotlin.test.Test

class SearchParamsTest {
    @Test
    fun `ClockCustomizationSearchParams.fromString is correct`() {
        ClockSearchParams.fromString("")!!.clock shouldbe null

        ClockSearchParams.fromString("clock=Form")!!.clock shouldbe ClockType.Form
        ClockSearchParams.fromString("clock=form")!!.clock shouldbe ClockType.Form
        ClockSearchParams.fromString("clock=FORM")!!.clock shouldbe ClockType.Form

        with(
            ClockSearchParams.fromString(
                "?clock=io18&layout=wrapped&background=123def&colors=ff0000,00ff00,0000ff,f0000f"
            )!!
        ) {
            clock shouldbe ClockType.Io18
            layout shouldbe Layout.Wrapped
            background shouldbe Color(0xff123def)
            colors shouldbe listOf(
                Color(0xffff0000),
                Color(0xff00ff00),
                Color(0xff0000ff),
                Color(0xfff0000f),
            )
        }
        with(
            ClockSearchParams.fromString(
                "background=131213&colors=4db49a%2C3698c6%2Cffffff%2Cadd5e7"
            )!!
        ) {
            colors shouldbe listOf(
                Color(0xff4db49a),
                Color(0xff3698c6),
                Color(0xffffffff),
                Color(0xffadd5e7),
            )
        }
    }
}
