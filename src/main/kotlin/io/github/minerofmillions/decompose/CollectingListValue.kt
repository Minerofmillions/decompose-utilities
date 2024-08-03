package io.github.minerofmillions.decompose

import com.arkivanov.decompose.value.Value

/**
 * Turns a [List] of [Value]s into a [Value]-wrapped [List] that updates every time the original's [Value]s do.
 */
fun <T : Any> List<Value<T>>.collect(): Value<List<T>> = CollectingListValue(this)

private class CollectingListValue<T : Any>(private val upstream: List<Value<T>>) : AbstractValue<List<T>>() {
    init {
        upstream.forEach {
            it.subscribe {
                updateValue()
            }
        }
    }

    override fun generateValue(): List<T> = upstream.map(Value<T>::value)
}