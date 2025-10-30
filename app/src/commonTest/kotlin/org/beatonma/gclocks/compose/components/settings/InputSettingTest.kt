package org.beatonma.gclocks.compose.components.settings

import org.beatonma.gclocks.test.shouldbe
import kotlin.test.Test

class InputSettingTest {
    @Test
    fun `summariseIntList is correct`() {
        summariseIntList(listOf()) shouldbe ""
        summariseIntList(listOf(1)) shouldbe "1"
        summariseIntList(listOf(1, 2)) shouldbe "1, 2"
        summariseIntList(listOf(1, 2, 3)) shouldbe "1-3"
        summariseIntList(listOf(1, 2, 3, 4, 5)) shouldbe "1-5"
        summariseIntList(listOf(1, 2, 3, 5)) shouldbe "1-3, 5"
        summariseIntList(listOf(1, 2, 3, 6, 8)) shouldbe "1-3, 6, 8"
        summariseIntList(listOf(2, 4, 6, 8)) shouldbe "2, 4, 6, 8"
        summariseIntList(listOf(6, 7, 8, 9, 21, 22, 23, 25)) shouldbe "6-9, 21-23, 25"
    }
}
