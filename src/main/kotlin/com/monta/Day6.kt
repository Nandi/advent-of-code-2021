package com.monta

fun main() {
    val input = "day6.txt"
        .loadResource()[0]
        .split(",")
        .map { it.toInt() }
        .groupBy { it }
        .mapValues { it.value.size.toLong() }
        .toSortedMap()

    val amount = countLanternFish(input)
    println(amount)
    val extended = countLanternFish(input, 256)
    println(extended)
}

private fun countLanternFish(initial: Map<Int, Long>, days: Int = 80): Long {
    val lanternFish = initial.toMutableMap()

    for (noop in 1..days) {
        val spawning = lanternFish[0] ?: 0L
        for (i in 1..8) {
            val fish = lanternFish[i] ?: 0L
            lanternFish[i] = 0L
            lanternFish[i - 1] = fish
        }
        lanternFish[6] = spawning + (lanternFish[6] ?: 0L)
        lanternFish[8] = spawning + (lanternFish[8] ?: 0L)
    }

    return lanternFish.values.sum()
}