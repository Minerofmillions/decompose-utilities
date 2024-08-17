package io.github.minerofmillions.decompose.samples

import com.arkivanov.decompose.value.Value
import io.github.minerofmillions.decompose.collect

/**
 * Collects a two-dimensional list of values to a value of a two-dimensional list.
 */
fun <T : Any> twoDimensionListCollect(twoDimensionValueList: List<List<Value<T>>>): Value<List<List<T>>> =
    twoDimensionValueList.collect { it.collect() }