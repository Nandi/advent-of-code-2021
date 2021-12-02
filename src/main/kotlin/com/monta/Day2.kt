package com.monta

fun main() {
    val input = "day2.txt".loadResource().filterNotBlank()

    val (x1, y1) = findFinalCoordinates(input)
    println(x1 * y1)

    val (x2, y2) = advancedGuidanceSystem(input)
    println(x2 * y2)
}

private fun findFinalCoordinates(commands: List<String>): Coordinates {
    val xPos = commands
        .filter { it.startsWith("forward") }
        .sumOf { it.split(" ")[1].toInt() }

    val yPos = commands
        .filterNot { it.startsWith("forward") }
        .sumOf {
            val (dir, amount) = it.split(" ")
            (amount.toInt()) * when (dir) {
                "up" -> -1
                else -> 1
            }
        }

    return Coordinates(xPos, yPos)
}

private fun advancedGuidanceSystem(commands: List<String>): Coordinates {
    var xPos = 0
    var yPos = 0
    var aim = 0

    for (command in commands) {
        val (direction, amount) = command.split(" ")
        when (direction) {
            "up" -> aim -= amount.toInt()
            "down" -> aim += amount.toInt()
            else -> {
                xPos += amount.toInt()
                yPos += (aim * amount.toInt())
            }
        }
    }

    return Coordinates(xPos, yPos)
}

data class Coordinates(val x: Int, val y: Int)