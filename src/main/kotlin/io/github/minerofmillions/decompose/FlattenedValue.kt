package io.github.minerofmillions.decompose

import com.arkivanov.decompose.Cancellation
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.getValue

fun <T : Any> Value<Value<T>>.flatten(): Value<T> = FlattenedValue(this)

private class FlattenedValue<T : Any>(private val upstream: Value<Value<T>>) : AbstractValue<T>() {
    private val currentUpstreamValue by upstream
    private var currentUpstreamObserver: Cancellation? = null
    init {
        upstream.subscribe {
            currentUpstreamObserver?.cancel()
            currentUpstreamObserver = it.subscribe {
                updateValue()
            }
        }
    }

    override fun generateValue(): T = currentUpstreamValue.value
}