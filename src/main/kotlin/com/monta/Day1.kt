package com.monta

fun main() {
    val input = "day1.txt".loadResource()
        .filterNotBlank()
        .map { it.toInt() }

    println(slidingWindowDepthMeasurement(input, 1))
    println(slidingWindowDepthMeasurement(input, 3))
}

private fun slidingWindowDepthMeasurement(measurements: List<Int>, windowSize: Int = 3): Int {
    var count = 0
    var prev = Int.MAX_VALUE

    val startPoint = windowSize - 1

    if (windowSize > measurements.size) return 0

    for (i in startPoint until measurements.size) {
        val amount = measurements.subList(i - startPoint, i + 1).sum()
        if (amount > prev) count++
        prev = amount
    }

    return count
}

