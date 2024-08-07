package io.github.minerofmillions.decompose

import com.arkivanov.decompose.Cancellation
import com.arkivanov.decompose.value.Value

/**
 * Makes a mutable list containing the given [elements] that can be subscribed to.
 *
 * @see collect
 */
fun <E> mutableValueListOf(vararg elements: E) = MutableValueList(listOf(*elements))

class MutableValueList<E> internal constructor(private var _value: List<E>) : MutableList<E>, Value<List<E>>() {
    private val observers = mutableMapOf<(List<E>) -> Unit, Boolean>()
    private val lock = Lock()
    private var isEmitting = false
    override val value: List<E> get() = lock.synchronized { _value }

    private fun setValue(value: List<E>): Boolean {
        lock.synchronized {
            if (_value.iterableEquals(value)) return false
            _value = value

            if (isEmitting) {
                return true
            }

            isEmitting = true
        }

        emit()

        return true
    }

    private fun emit() {
        while (true) {
            val value: List<E>
            val observersCopy: Map<(List<E>) -> Unit, Boolean>

            lock.synchronized {
                value = _value
                observersCopy = observers
            }

            observersCopy.forEach { (observer, isEnabled) ->
                if (isEnabled) {
                    observer(value)
                }
            }

            lock.synchronized {
                if (value === _value) {
                    isEmitting = false
                    return
                }
            }
        }
    }

    override fun subscribe(observer: (List<E>) -> Unit): Cancellation {
        subscribeObserver(observer)

        return Cancellation { unsubscribeObserver(observer) }
    }

    private fun subscribeObserver(observer: (List<E>) -> Unit) {
        lock.synchronized {
            if (observer in observers) {
                return
            }

            observers += observer to false
        }

        while (true) {
            val value = lock.synchronized { _value }

            observer(value)

            lock.synchronized {
                if (observer !in observers) {
                    return
                }

                if (value === _value) {
                    observers += observer to true
                    return
                }
            }
        }
    }

    private fun unsubscribeObserver(observer: (List<E>) -> Unit) {
        lock.synchronized { observers -= observer }
    }

    override val size: Int
        get() = value.size

    override fun clear() {
        setValue(emptyList())
    }

    override fun get(index: Int): E = value[index]

    override fun isEmpty(): Boolean = value.isEmpty()

    override fun iterator(): MutableIterator<E> = MVLIterator(this)

    override fun listIterator(): MutableListIterator<E> = MVLIterator(this)

    override fun listIterator(index: Int): MutableListIterator<E> = MVLIterator(this, index)

    override fun removeAt(index: Int): E {
        val prevValue = get(index)
        if (!setValue(value.subList(0, index) + value.subList(index + 1, value.size)))
            error("Failed to removeAt $index")
        return prevValue
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> {
        val view = MutableValueList(value.subList(fromIndex, toIndex))
        view.subscribe { sublist ->
            setValue(value.subList(0, fromIndex) + sublist + value.subList(toIndex, size))
        }
        return view
    }

    override fun set(index: Int, element: E): E {
        val prevValue = get(index)
        if (prevValue == element) return prevValue
        if (!setValue(value.subList(0, index) + element + value.subList(index + 1, size)))
            error("Failed to set value.")
        return prevValue
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        return setValue(value.filter(elements::contains))
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        return setValue(value.filterNot(elements::contains))
    }

    override fun remove(element: E): Boolean {
        val index = indexOf(element).takeIf { it > -1 } ?: return false
        return setValue(value.subList(0, index) + value.subList(index + 1, size))
    }

    override fun lastIndexOf(element: E): Int = value.lastIndexOf(element)

    override fun indexOf(element: E): Int = value.indexOf(element)

    override fun containsAll(elements: Collection<E>): Boolean = value.containsAll(elements)

    override fun contains(element: E): Boolean = value.contains(element)

    override fun addAll(elements: Collection<E>): Boolean = setValue(value + elements)

    override fun addAll(index: Int, elements: Collection<E>): Boolean =
        setValue(value.subList(0, index) + elements + value.subList(index, size))

    override fun add(index: Int, element: E) {
        setValue(slice(0 until index) + element + slice(index until size))
    }

    override fun add(element: E): Boolean = setValue(value + element)

    private class MVLIterator<E>(private val list: MutableValueList<E>, private var index: Int = 0) :
        MutableListIterator<E> {
        private var lastIndex = -1

        override fun hasPrevious(): Boolean = index > 0

        override fun add(element: E) {
            list.add(index++, element)
            lastIndex = -1
        }

        override fun hasNext(): Boolean = index < list.size

        override fun next(): E {
            if (index >= list.size) throw NoSuchElementException()
            lastIndex = index
            return list[index++]
        }

        override fun nextIndex(): Int = index + 1

        override fun previous(): E {
            if (index <= 0) throw NoSuchElementException()
            lastIndex = --index
            return list[lastIndex]
        }

        override fun previousIndex(): Int = index - 1

        override fun remove() {
            check(lastIndex != -1) { "Call next() or previous() before removing element from the iterator." }
            list.removeAt(lastIndex)
            index = lastIndex
            lastIndex = -1
        }

        override fun set(element: E) {
            check(lastIndex != -1) { "Call next() or previous() before replacing element from the iterator." }
            list[lastIndex] = element
        }
    }
}

internal fun <T> Iterable<T>.iterableEquals(other: Iterable<T>): Boolean {
    val myIterator = iterator()
    val otherIterator = other.iterator()

    while (myIterator.hasNext() && otherIterator.hasNext()) {
        if (myIterator.next() != otherIterator.next()) return false
    }

    return !myIterator.hasNext() && !otherIterator.hasNext()
}