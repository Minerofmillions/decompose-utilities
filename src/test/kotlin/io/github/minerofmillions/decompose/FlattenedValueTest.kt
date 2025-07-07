package io.github.minerofmillions.decompose

import com.arkivanov.decompose.value.MutableValue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FlattenedValueTest {
    @Test
    fun value() {
        val inner = MutableValue("Hello")
        val outer = MutableValue(inner)
        val flattened = outer.flatten()

        assertEquals("Hello", flattened.value)

        inner.value = "World"
        assertEquals("World", flattened.value)

        val secondInner = MutableValue("Testing")
        outer.value = secondInner
        assertEquals("Testing", flattened.value)
        secondInner.value = "Things"
        assertEquals("Things", flattened.value)
    }

    @Test
    fun valueObserved() {
        val inner = MutableValue("Hello")
        val outer = MutableValue(inner)
        val flattened = outer.flatten()
        var expected = "Hello"
        val cancellation = flattened.subscribe { assertEquals(expected, it) }
        expected = "World"
        inner.value = "World"

        val secondInner = MutableValue("Testing")
        expected = "Testing"
        outer.value = secondInner

        expected = "Things"
        secondInner.value = "Things"

        cancellation.cancel()
    }
}