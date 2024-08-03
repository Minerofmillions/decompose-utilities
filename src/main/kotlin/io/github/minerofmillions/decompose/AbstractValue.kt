package io.github.minerofmillions.decompose

import com.arkivanov.decompose.Cancellation
import com.arkivanov.decompose.value.Value


/**
 * An abstract class allowing for easier generation of custom [Value]s.
 */
abstract class AbstractValue<T : Any> : Value<T>() {
    private val lock = Lock()
    private lateinit var _value : T
    private var isEmitting = false
    private val observers = mutableMapOf<(T) -> Unit, Boolean>()

    override val value: T get() = lock.synchronized { _value }

    protected abstract fun generateValue(): T

    protected fun updateValue() {
        val value = generateValue()

        lock.synchronized {
            _value = value

            if (isEmitting) {
                return
            }

            isEmitting = true
        }

        emit()
    }


    private fun emit() {
        while (true) {
            val value: T
            val observersCopy: Map<(T) -> Unit, Boolean>

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


    override fun subscribe(observer: (T) -> Unit): Cancellation {
        subscribeObserver(observer)

        return Cancellation { unsubscribeObserver(observer) }
    }

    private fun subscribeObserver(observer: (T) -> Unit) {
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

    private fun unsubscribeObserver(observer: (T) -> Unit) {
        lock.synchronized { observers -= observer }
    }
}