package com.monta

fun main() {
    val input = "day10.txt".loadResource().filterNotBlank()
    println(findCorruptedLines(input))
    println(completeIncompleteLines(input))
}

private fun findCorruptedLines(lines: List<String>): Long {
    val errors = mutableMapOf<Char, Int>()

    for (line in lines) {
        line.validate().forEach { errors.compute(it.key) { _, oldValue -> it.value + (oldValue ?: 0) } }
    }

    return errors.map { corruptedScoreTable[it.key]!! * it.value }.sumOf { it.toLong() }

}

private fun completeIncompleteLines(lines: List<String>): Long {
    val scores = mutableListOf<Long>()

    for (line in lines) {
        if (line.validate().isNotEmpty()) continue
        var latestChunk = stackOf<Char>()
        for (c in line) {
            if ("""[(\[{<]""".toRegex().containsMatchIn("$c")) {
                latestChunk.push(c)
            } else {
                latestChunk.pop()
            }
        }

        latestChunk = (latestChunk.map { closingTable[it]!! }.reversed() as Stack<Char>)

        var score = 0L
        for (c in latestChunk) {
            score = score * 5 + incompleteScoreTable[c]!!
        }
        println("${latestChunk.joinToString("")} -> $score")
        scores += score
    }

    return scores.sorted()[scores.size / 2]
}

private fun String.validate(): Map<Char, Int> {
    val errors = mutableMapOf<Char, Int>()
    val latestChunk = stackOf<Char>()
    for (c in this) {
        if ("""[(\[{<]""".toRegex().containsMatchIn("$c")) {
            latestChunk.push(c)
        } else if (c != closingTable[latestChunk.peek()]) {
            errors.compute(c) { _, oldValue -> 1 + (oldValue ?: 0) }
            break
        } else {
            latestChunk.pop()
        }
    }
    return errors
}

val closingTable = mapOf(
    '(' to ')',
    '[' to ']',
    '{' to '}',
    '<' to '>',
)

val incompleteScoreTable = mapOf(
    ')' to 1,
    ']' to 2,
    '}' to 3,
    '>' to 4,
)

val corruptedScoreTable = mapOf(
    ')' to 3,
    ']' to 57,
    '}' to 1197,
    '>' to 25137,
)

typealias Stack<T> = MutableList<T>

private fun <T> stackOf(): Stack<T> = ArrayList()

private fun <T> Stack<T>.pop() = this.removeAt(lastIndex)
private fun <T> Stack<T>.push(item: T) = this.add(item)
private fun <T> Stack<T>.peek() = this[lastIndex]