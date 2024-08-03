package io.github.minerofmillions.decompose

import com.arkivanov.decompose.value.MutableValue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CollectingListValueTest {
    @Test
    fun collect() {
        val list = (0 until 5).map(::MutableValue)
        val collected = list.collect()

        Assertions.assertIterableEquals((0 until 5), collected.value)

        list[0].value = 6
        Assertions.assertIterableEquals(listOf(6, 1, 2, 3, 4), collected.value)
    }

    @Test
    fun collectSubscribe() {
        val list = (0 until 5).map(::MutableValue)
        val collected = list.collect()
        var expected = listOf(0, 1, 2, 3, 4)

        collected.subscribe {
            Assertions.assertIterableEquals(expected, it)
        }

        expected = listOf(6, 1, 2, 3, 4)
        list[0].value = 6

        expected = listOf(6, 1, 3, 3, 4)
        list[2].value = 3
    }
}