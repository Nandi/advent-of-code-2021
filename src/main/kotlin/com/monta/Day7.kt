package com.monta

import kotlin.math.abs

fun main() {
    val input = "day7.txt"
        .loadResource()[0]
        .split(",")
        .map { it.toInt() }

    val simpleFuelUse = crabAlignment(input)
    println(simpleFuelUse)
    val actualFuelUse = crabAlignment(input) { n -> n * (n + 1) / 2 }
    println(actualFuelUse)
}

fun crabAlignment(initialPositions: List<Int>, fuelRate: (Long) -> Long = { it }): Long {
    val positions = initialPositions.toMutableList()
    val min = positions.minOf { it }
    val max = positions.maxOf { it }

    var lowest = Long.MAX_VALUE

    for (alignAt in min..max) {
        val fuelUse = positions.sumOf {
            val distance = abs(it - alignAt).toLong()
            fuelRate(distance)
        }
        if (lowest > fuelUse) lowest = fuelUse
    }

    return lowest
}