package io.github.minerofmillions.utils

infix fun <E> Collection<E>.contentsEqual(other: Collection<E>): Boolean =
    this.all(other::contains) && other.all(this::contains)
