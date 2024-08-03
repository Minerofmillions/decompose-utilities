package io.github.minerofmillions.decompose

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MutableValueListTest {
    @Test
    fun mutableValueListValue() {
        val list = mutableValueListOf<Int>()
        Assertions.assertIterableEquals(emptyList<Int>(), list.value)
        list.add(1)
        Assertions.assertIterableEquals(listOf(1), list.value)
        list.addAll(3 until 5)
        Assertions.assertIterableEquals(listOf(1, 3, 4), list.value)
    }

    @Test
    fun mutableValueListObserve() {
        var expected = emptyList<Int>()
        val observer: (List<Int>) -> Unit = { Assertions.assertIterableEquals(expected, it) }
        val list = mutableValueListOf<Int>()
        val disposable = list.subscribe(observer)

        expected = listOf(1)
        list.add(1)

        expected = listOf(1, 3, 4)
        list.addAll((3 until 5).toList())

        disposable.cancel()
    }
}