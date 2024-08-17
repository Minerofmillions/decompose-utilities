package io.github.minerofmillions.decompose

import com.arkivanov.decompose.value.Value

/**
 * Turns a [Collection] of [Value]s into a Value-wrapped List that updates every time the original's Values do.
 *
 * @return A Value<List<T>> that updates every time any of the receiver's Values do.
 */
fun <T : Any> Collection<Value<T>>.collect(): Value<List<T>> = CollectingListValue(this)

/**
 * Transforms an [Iterable] into a [Value]-wrapped [List] that updates every time the transformed Values do.
 *
 * Equivalent to calling [Iterable.map] with the given [transform] then [collect].
 *
 * @param transform A mapping from element to Value to watch and collect.
 * @return A Value<List<R>> that updates every time any of the transformed Values do.
 *
 * @sample io.github.minerofmillions.decompose.samples.twoDimensionListCollect
 */
fun <T, R : Any> Iterable<T>.collect(transform: (T) -> Value<R>): Value<List<R>> = CollectingListValue(map(transform))

fun <T : Any> Array<out Value<T>>.collect(): Value<List<T>> = CollectingListValue(toList())

fun <T, R : Any> Array<out T>.collect(transform: (T) -> Value<R>): Value<List<R>> = CollectingListValue(map(transform))

private class CollectingListValue<T : Any>(private val upstream: Collection<Value<T>>) : AbstractValue<List<T>>() {
    init {
        upstream.forEach {
            it.subscribe {
                updateValue()
            }
        }
    }

    override fun generateValue(): List<T> = upstream.map(Value<T>::value)
}