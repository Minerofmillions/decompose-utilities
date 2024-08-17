package io.github.minerofmillions.decompose.samples

import com.arkivanov.decompose.value.Value
import io.github.minerofmillions.decompose.collect

/**
 * Collects a two-dimensional list of values to a value of a two-dimensional list.
 */
fun <T : Any> twoDimensionListCollect(twoDimensionValueList: List<List<Value<T>>>) =
    twoDimensionValueList.collect { it.collect() }

fun <T : Any> twoDimensionArrayCollect(twoDimensionValueArray: Array<Array<out Value<T>>>) =
    twoDimensionValueArray.collect { it.collect() }