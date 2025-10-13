package org.beatonma.gclocks.app.theme

import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.beatonma.gclocks.app.data.settings.ClockType
import org.beatonma.gclocks.compose.components.ButtonColors

internal sealed interface ClockColorScheme {
    val backgroundColor: Color
    val clockColors: List<Color>
    val Vibrant: Color
    val Muted: Color

    val buttonColors: ButtonColors
        get() = ButtonColors(
            selectedContainerColor = Vibrant,
            unselectedContainerColor = Muted
        )
    val cardColors: CardColors
        @Composable
        get() {
            val containerColor = colorScheme.surface
            return CardColors(
                containerColor = containerColor,
                contentColor = containerColor.getForegroundColor(),
                disabledContainerColor = containerColor,
                disabledContentColor = containerColor.getForegroundColor(Opacity.TextSecondary),
            )
        }

    object Form : ClockColorScheme {
        private val Orange = Color(0xffFF6D00)
        private val Yellow = Color(0xffFFC400)
        private val White = Color.White

        override val backgroundColor = Color(0xFF277CCC)
        override val clockColors = listOf(Orange, Yellow, White)

        override val Vibrant: Color = Orange
        override val Muted: Color = Color(0xFFb59d8b)
    }

    object Io16 : ClockColorScheme {
        private val Red = Color(0xffef5350)
        private val Cyan = Color(0xff8cf2f2)
        private val DarkCyan = Color(0xff33c9dc)
        private val Indigo = Color(0xff5c6bc0)
        private val Inactive = Color(0xff78909c)

        override val backgroundColor = Color(0xFF4C4C45)
        override val clockColors = listOf(Red, Cyan, DarkCyan, Indigo, Inactive)

        override val Vibrant: Color = Red
        override val Muted: Color = Color(0xffb08f8f)
    }

    object Io18 : ClockColorScheme {
        private val Turquoise = Color(0xff1de9b6)
        private val Orange = Color(0xffff6c00)
        private val Yellow = Color(0xfffdd835)
        private val Blue = Color(0xff536dfe)

        override val backgroundColor = Color(0xFF2D2D2D)
        override val clockColors = listOf(
            Turquoise,
            Orange,
            Yellow,
            Blue,
        )

        override val Vibrant: Color = Blue
        override val Muted: Color = Color(0xff999eb8)
    }
}

fun ClockType.colorScheme() = when (this) {
    ClockType.Form -> ClockColorScheme.Form.buttonColors
    ClockType.Io16 -> ClockColorScheme.Io16.buttonColors
    ClockType.Io18 -> ClockColorScheme.Io18.buttonColors
}
