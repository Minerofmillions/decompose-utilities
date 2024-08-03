package io.github.minerofmillions.decompose

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Test

class FilteredValueTest {
    @Test
    fun value() {
        val list = (0..5).map(::MutableValue)
        val filtered = list.filter { value: Value<Int> -> value.map { it % 2 == 0 } }
        fun getValue() = filtered.value.map(MutableValue<Int>::value)
        assertIterableEquals(listOf(0, 2, 4), getValue())
        list[5].value = 20
        assertIterableEquals(listOf(0, 2, 4, 20), getValue())
        list[0].value = 5
        assertIterableEquals(listOf(2, 4, 20), getValue())
    }

    @Test
    fun valueObserved() {
        var expected = listOf(0, 2, 4)
        val observer: (List<Value<Int>>) -> Unit = { assertIterableEquals(expected, it.map(Value<Int>::value)) }
        val list = (0..5).map(::MutableValue)
        val filtered = list.filter { value: Value<Int> -> value.map { it % 2 == 0 } }
        val disposable = filtered.subscribe(observer)
        expected = listOf(0, 2, 4, 20)
        list[5].value = 20
        expected = listOf(2, 4, 20)
        list[0].value = 5
        expected = listOf(4, 20)
        list[2].value = 3
        expected = listOf(20)
        list[4].value = 5
        list[0].value = 1
        expected = emptyList()
        list[5].value = 5
        disposable.cancel()
    }
}