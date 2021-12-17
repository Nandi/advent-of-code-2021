package com.monta

fun main() {
    val input = "day4.txt".loadResource().filterNotBlank()
    val numbers = parseBingoNumbers(input.first())
    val bingoBoards = parseBoards(input.drop(1))

    val best = findBestBoard(numbers, bingoBoards)
    println(best)

    val worst = findWorstBoard(numbers, bingoBoards)
    println(worst)

}

private fun parseBingoNumbers(numbers: String): List<Int> {
    return numbers.split(",").map { it.toInt() }
}

private fun parseBoards(boardsList: List<String>): List<BingoBoard> {
    val boards = mutableListOf<BingoBoard>()
    for (i in boardsList.indices step 5) {
        val board = Array(5) { index ->
            boardsList[i + index].trim().split("\\s+".toRegex()).map { it.toInt() }.toIntArray()
        }
        boards.add(BingoBoard(board))
    }

    return boards
}

private fun findBestBoard(numbers: List<Int>, boards: List<BingoBoard>): Int {
    var latest = 0
    for (number in numbers) {
        latest = number
        boards.forEach { it.checkAndMark(latest) }
        if (boards.any { it.checkForCompleted() }) break
    }

    val winner = boards.first { it.checkForCompleted() }

    return winner.sum().times(latest)
}

private fun findWorstBoard(numbers: List<Int>, boards: List<BingoBoard>): Int {
    var latest = 0

    val worstBoards = boards.toMutableList()
    var worstBoard: BingoBoard? = null

    for (number in numbers) {
        latest = number
        worstBoards.removeIf { it.checkForCompleted() }
        worstBoards.forEach { it.checkAndMark(latest) }
        worstBoard = worstBoards.firstOrNull { it.checkForCompleted() }
        if (worstBoards.size == 1) break
    }

    val winner = worstBoard!!

    return winner.sum().times(latest)
}

class BingoBoard(private val board: Array<IntArray>) {
    fun checkAndMark(number: Int) {
        for (i in board.indices) {
            for (j in board[i].indices) {
                if (board[i][j] == number) {
                    board[i][j] = 0
                }
            }
        }
    }

    fun checkForCompleted(): Boolean {
        // check rows
        val rowResult = board.any { it.sum() == 0 }

        // check columns
        var colResult = false
        for (i in 0..4) {
            colResult = colResult || board.sumOf { it[i] } == 0
        }

        return rowResult || colResult
    }

    fun sum(): Int {
        return board.sumOf { it.sum() }
    }
}