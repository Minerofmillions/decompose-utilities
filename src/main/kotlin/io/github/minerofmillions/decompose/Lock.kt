package io.github.minerofmillions.decompose

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Lock {
    @Suppress("LEAKED_IN_PLACE_LAMBDA", "WRONG_INVOCATION_KIND")
    @OptIn(ExperimentalContracts::class)
    internal inline fun <T> synchronized(block: () -> T): T {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

        return synchronized(this, block)
    }

}