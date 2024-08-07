package io.github.minerofmillions.decompose

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MutableValueListTest {
    @Test
    fun mutableValueListValue() {
        val list = mutableValueListOf<Int>()
        assertIterableEquals(emptyList<Int>(), list.value)
        list.add(1)
        assertIterableEquals(listOf(1), list.value)
        list.addAll(3 until 5)
        assertIterableEquals(listOf(1, 3, 4), list.value)
    }

    @Test
    fun mutableValueListObserve() {
        var expected = emptyList<Int>()
        val observer: (List<Int>) -> Unit = { assertIterableEquals(expected, it) }
        val list = mutableValueListOf<Int>()
        val disposable = list.subscribe(observer)

        expected = listOf(1)
        list.add(1)

        expected = listOf(1, 3, 4)
        list.addAll((3 until 5).toList())

        disposable.cancel()
    }

    @Test
    fun iterableEquals() {
        assertTrue(listOf(1, 2, 3).iterableEquals(1..3))
        assertTrue((1..4).iterableEquals(listOf(1, 2, 3, 4)))
    }

    @Test
    fun getSize() {
        assertEquals(0, mutableValueListOf<Int>().size)
        assertEquals(1, mutableValueListOf(1).size)
        assertEquals(4, mutableValueListOf(0, 1, 2, 3).size)
    }

    @Test
    fun clear() {
        val list = mutableValueListOf(1, 2, 3)
        assertEquals(3, list.size)
        list.clear()
        assertEquals(0, list.size)
    }

    @Test
    fun get() {
        val list = mutableValueListOf(1, 2, 3)
        assertEquals(1, list[0])
        assertEquals(2, list[1])
        assertEquals(3, list[2])
        assertThrows<IndexOutOfBoundsException> { list[-1] }
        assertThrows<IndexOutOfBoundsException> { list[3] }
    }

    @Test
    fun isEmpty() {
        assertTrue(mutableValueListOf<Int>().isEmpty())
        assertFalse(mutableValueListOf(1, 2, 3).isEmpty())
    }

    @Test
    fun removeAt() {
        val list = mutableValueListOf(1, 2, 3)
        assertEquals(2, list.removeAt(1))
        assertEquals(2, list.size)

        assertEquals(1, list.removeAt(0))
        assertEquals(1, list.size)
    }

    @Test
    fun subList() {
        val list = mutableValueListOf(1, 2, 3, 4)

        assertIterableEquals(1..3, list.subList(0, 3))
        assertIterableEquals(3..4, list.subList(2, 4))
    }

    @Test
    fun set() {
        val list = mutableValueListOf(1, 2, 3, 4)

        assertEquals(2, list.set(1, 5))
        assertIterableEquals(listOf(1, 5, 3, 4), list)

        assertEquals(4, list.set(3, 6))
        assertIterableEquals(listOf(1, 5, 3, 6), list)
    }

    @Test
    fun retainAll() {
        val list = mutableValueListOf(1, 2, 3, 4)
        assertTrue(list.retainAll(listOf(3, 1, 2)))
        assertIterableEquals(listOf(1, 2, 3), list)

        assertTrue(list.retainAll(listOf(4)))
        assertIterableEquals(emptyList<Int>(), list)
    }

    @Test
    fun removeAll() {
        val list = mutableValueListOf(1, 2, 3, 4)
        assertTrue(list.removeAll(listOf(4)))
        assertIterableEquals(listOf(1, 2, 3), list)

        assertTrue(list.removeAll(listOf(3, 1)))
        assertIterableEquals(listOf(2), list)
    }

    @Test
    fun remove() {
        val list = mutableValueListOf(1, 2, 3, 4)
        assertFalse(list.remove(6))

        assertTrue(list.remove(3))
        assertIterableEquals(listOf(1, 2, 4), list)

        assertFalse(list.remove(3))

        assertTrue(list.remove(1))
        assertIterableEquals(listOf(2, 4), list)

        assertTrue(list.remove(4))
        assertIterableEquals(listOf(2), list)
    }

    @Test
    fun lastIndexOf() {
        val list = mutableValueListOf(1, 2, 3, 4, 3, 2, 1)

        assertEquals(6, list.lastIndexOf(1))
        assertEquals(5, list.lastIndexOf(2))
        assertEquals(4, list.lastIndexOf(3))
        assertEquals(3, list.lastIndexOf(4))
        assertEquals(-1, list.lastIndexOf(5))
    }

    @Test
    fun indexOf() {
        val list = mutableValueListOf(1, 2, 3, 4, 3, 2, 1)

        assertEquals(0, list.indexOf(1))
        assertEquals(1, list.indexOf(2))
        assertEquals(2, list.indexOf(3))
        assertEquals(3, list.indexOf(4))
        assertEquals(-1, list.indexOf(5))
    }

    @Test
    fun containsAll() {
        val list = mutableValueListOf(1, 2, 3, 4)

        assertTrue(list.containsAll(listOf(1, 3, 2)))
        assertTrue(list.containsAll(listOf(4)))
        assertFalse(list.containsAll(listOf(1, 3, 5)))
        assertFalse(list.containsAll(listOf(6, 7)))
    }

    @Test
    fun contains() {
        val list = mutableValueListOf(1, 2, 3, 4)

        assertTrue(list.contains(1))
        assertTrue(list.contains(2))
        assertTrue(list.contains(3))
        assertTrue(list.contains(4))
        assertFalse(list.contains(5))
        assertFalse(list.contains(-10))
    }

    @Test
    fun addAll() {
        val list = mutableValueListOf<Int>()

        assertTrue(list.addAll(listOf(1, 2)))
        assertIterableEquals(listOf(1, 2), list)

        assertTrue(list.addAll(listOf(5, 6)))
        assertIterableEquals(listOf(1, 2, 5, 6), list)

        assertFalse(list.addAll(emptyList()))
        assertIterableEquals(listOf(1, 2, 5, 6), list)
    }

    @Test
    fun addAllIndexed() {
        val list = mutableValueListOf<Int>()

        assertTrue(list.addAll(listOf(1, 2)))
        assertIterableEquals(listOf(1, 2), list)

        assertTrue(list.addAll(1, listOf(5, 6)))
        assertIterableEquals(listOf(1, 5, 6, 2), list)

        assertFalse(list.addAll(4, emptyList()))
        assertIterableEquals(listOf(1, 5, 6, 2), list)
    }

    @Test
    fun add() {
        val list = mutableValueListOf<Int>()

        assertTrue(list.add(1))
        assertIterableEquals(listOf(1), list)

        assertTrue(list.add(5))
        assertIterableEquals(listOf(1, 5), list)

        assertTrue(list.add(1))
        assertIterableEquals(listOf(1, 5, 1), list)
    }

    @Test
    fun addIndexed() {
        val list = mutableValueListOf<Int>()

        list.add(1)
        list.add(0, 5)
        assertIterableEquals(listOf(5, 1), list)

        list.add(2, 1)
        assertIterableEquals(listOf(5, 1, 1), list)
    }
}