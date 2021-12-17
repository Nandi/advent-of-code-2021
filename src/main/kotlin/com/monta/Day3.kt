package com.monta

fun main() {
    val input = "day3.txt".loadResource().filterNotBlank()
    val powerConsumption = calculatePowerConsumption(input)
    println(powerConsumption)

    val lifeSupportRating = calculateLifeSupportRating(input)
    println(lifeSupportRating)
}

private fun calculatePowerConsumption(diagnostics: List<String>): Long {
    val size = diagnostics[0].length
    val bitColumns = Array(size) { index ->
        buildString {
            diagnostics.forEach { append(it[index]) }
        }
    }

    val breakPoint = (diagnostics.size / 2)


    val gamma = bitColumns.map { bits -> bits.map { it.digitToInt() }.sum() }
        .joinToString("") { if (it < breakPoint) "0" else "1" }

    val sigma = gamma.flip()

    return gamma.toLong(2) * sigma.toLong(2)
}

private fun calculateLifeSupportRating(diagnostics: List<String>): Long {
    val oxygenRating = calculateOxygenGeneratorRating(diagnostics)
    val co2ScrubberRating = calculateCo2ScrubberRating(diagnostics)

    return oxygenRating * co2ScrubberRating
}

private fun calculateCo2ScrubberRating(diagnostics: List<String>): Long {
    val size = diagnostics[0].length
    var co2RatingRatings = diagnostics.map { it }
    var column = 0
    while (true) {
        if (column >= size || co2RatingRatings.size < 2) break
        val bitColumns = Array(size) { index ->
            buildString {
                co2RatingRatings.forEach { append(it[index]) }
            }
        }

        co2RatingRatings = co2RatingRatings.filter { it[column] == mostSignificant(bitColumns[column]).flip() }
        column++
    }
    return co2RatingRatings.first().toLong(2)
}

private fun calculateOxygenGeneratorRating(diagnostics: List<String>): Long {
    val size = diagnostics[0].length
    var oxygenRatings = diagnostics.map { it }
    var column = 0
    while (true) {
        if (column >= size || oxygenRatings.size < 2) break
        val bitColumns = Array(size) { index ->
            buildString {
                oxygenRatings.forEach { append(it[index]) }
            }
        }

        oxygenRatings = oxygenRatings.filter { it[column] == mostSignificant(bitColumns[column]) }
        column++
    }
    return oxygenRatings.first().toLong(2)
}

private fun mostSignificant(column: String): Char {
    val sum = column.map { it.digitToInt() }.sum()
    val breakPoint = column.length / 2.0
    return when {
        sum < breakPoint -> '0'
        else -> '1'
    }
}

private fun Char.flip(): Char = if (this == '1') '0' else '1'

private fun String.flip(): String {
    return map { if (it == '1') '0' else '1' }.joinToString("")
}