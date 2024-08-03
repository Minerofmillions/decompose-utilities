package io.github.minerofmillions.decompose

import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map

/**
 * Returns a [Value] representing a list containing only elements that have a true [Value] associated with it by the given [predicate]
 */
fun <T> List<T>.filter(predicate: (T) -> Value<Boolean>): Value<List<T>> = FilteredValue(this, predicate)

/**
 * Returns a [Value] representing a list containing only elements that have a false [Value] associated with it by the given [predicate]
 */
fun <T> List<T>.filterNot(predicate: (T) -> Value<Boolean>): Value<List<T>> =
    FilteredValue(this) { predicate(it).map(Boolean::not) }

private class FilteredValue<T>(upstream: List<T>, predicate: (T) -> Value<Boolean>) : AbstractValue<List<T>>() {
    private val upstream = upstream.associateWith(predicate)

    init {
        this.upstream.values.forEach {
            it.subscribe { updateValue() }
        }
    }

    override fun generateValue(): List<T> = upstream.filterValues(Value<Boolean>::value).keys.toList()
}

