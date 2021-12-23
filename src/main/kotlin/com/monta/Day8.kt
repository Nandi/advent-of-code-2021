package com.monta

fun main() {
    val input = "day8.txt".loadResource().filterNotBlank()
    val unique = identifyUniqueSequences(input)
    println(unique)
    val sum = decodeSequences(input)
    println(sum)
}

private fun identifyUniqueSequences(data: List<String>): Int {
    val uniqueSequenceSizes = listOf(2, 4, 3, 7)
    return data
        .flatMap { it.split(" | ")[1].split(" ") }
        .count { it.length in uniqueSequenceSizes }
}

private fun decodeSequences(data: List<String>): Long {
    val numbers = mutableListOf<Long>()
    for (sequence in data) {
        val (wireList, digitsList) = sequence.split(" | ")
        val wires = wireList.split(" ").toList()
        val digits = digitsList.split(" ").toList()

        val wireCount = wireList.replace(" ", "").groupBy { it }.mapValues { it.value.size }
        val b = wireCount.filterValues { it == 6 }.keys.first()
        val e = wireCount.filterValues { it == 4 }.keys.first()
        val f = wireCount.filterValues { it == 9 }.keys.first()

        val c = wires.first { it.length == 2 }.first { it != f }
        val a = wires.first { it.length == 3 }.first { it !in listOf(c, f) }
        val d = wires.first { it.length == 4 }.first { it !in listOf(b, c, f) }
        val g = wires.first { it.length == 7 }.first { it !in listOf(a, b, c, d, e, f) }

        numbers += digits.joinToString("") { it.decode(a, b, c, d, e, f, g) }.toLong()
    }

    return numbers.sum()
}

private val segmentNumbers = mapOf(
    "abcefg" to "0",
    "cf" to "1",
    "acdeg" to "2",
    "acdfg" to "3",
    "bcdf" to "4",
    "abdfg" to "5",
    "abdefg" to "6",
    "acf" to "7",
    "abcdefg" to "8",
    "abcdfg" to "9"
)

private fun String.decode(a: Char, b: Char, c: Char, d: Char, e: Char, f: Char, g: Char): String {
    val lookup = mapOf(a to 'a', b to 'b', c to 'c', d to 'd', e to 'e', f to 'f', g to 'g')
    val decoded = this.map { lookup[it]!! }.sorted().joinToString("")
    return segmentNumbers[decoded]!!
}
