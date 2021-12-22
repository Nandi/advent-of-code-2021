package com.monta

import com.monta.LineSegmentDirection.*
import java.lang.Integer.max
import kotlin.math.min

fun main() {
    val input = "day5.txt".loadResource().filterNotBlank()
    val lineSegments = parseLineSegments(input)
    val orthogonalOverlap = locateIntersections(lineSegments) { it.direction != DIAGONAL }
    println(orthogonalOverlap)
    val overlap = locateIntersections(lineSegments)
    println(overlap)
}

private fun parseLineSegments(input: List<String>): List<LineSegment> {
    return input.map {
        val (start, end) = it.split(" -> ")
        val (x1, y1) = start.split(",")
        val (x2, y2) = end.split(",")
        LineSegment(Point(x1.toInt(), y1.toInt()), Point(x2.toInt(), y2.toInt()))
    }
}

private fun locateIntersections(lineSegments: List<LineSegment>, filter: (LineSegment) -> Boolean = { true }): Int {

    val cols = lineSegments.maxOf { max(it.p1.y, it.p2.y) } + 1
    val rows = lineSegments.maxOf { max(it.p1.x, it.p2.x) } + 1

    val grid = Array(cols) {
        IntArray(rows)
    }

    lineSegments.filter { filter(it) }.forEach {
        when (it.direction) {
            VERTICAL -> {
                val x = it.p1.x
                it.range.forEach { y -> grid[y][x]++ }
            }
            HORIZONTAL -> {
                val y = it.p1.y
                it.range.forEach { x -> grid[y][x]++ }
            }
            DIAGONAL -> {
                (it.p1..it.p2).forEach { p ->
                    grid[p.y][p.x]++
                }
            }
        }
    }

    return grid.flatMap { row -> row.filter { it > 1 }.map { 1 } }.count()
}

class LineSegment(
    val p1: Point,
    val p2: Point,
) {
    val direction: LineSegmentDirection = when {
        p1.x == p2.x -> VERTICAL
        p1.y == p2.y -> HORIZONTAL
        else -> DIAGONAL
    }
    val range: IntRange = when (direction) {
        VERTICAL -> (min(p1.y, p2.y)..max(p1.y, p2.y))
        HORIZONTAL -> (min(p1.x, p2.x)..max(p1.x, p2.x))
        DIAGONAL -> (min(p1.y, p2.y)..max(p1.y, p2.y))
    }

    override fun toString(): String {
        return "$p1 -> $p2"
    }
}

data class Point(val x: Int, val y: Int) : Comparable<Point> {
    operator fun rangeTo(other: Point) = PointProgression(this, other)

    override fun toString(): String {
        return "$x,$y"
    }

    override fun compareTo(other: Point): Int {
        return (this.x - other.x) + (this.y - other.y)
    }
}

class PointIterator(
    start: Point,
    private val endInclusive: Point,
) : Iterator<Point> {
    private var currentPoint = start

    private val xDirection = if ((endInclusive.x - start.x) < 0) -1 else 1
    private val yDirection = if ((endInclusive.y - start.y) < 0) -1 else 1


    override fun hasNext(): Boolean {
        val okX = when (xDirection) {
            -1 -> currentPoint.x >= endInclusive.x
            else -> currentPoint.x <= endInclusive.x
        }

        val okY = when (yDirection) {
            -1 -> currentPoint.y >= endInclusive.y
            else -> currentPoint.y <= endInclusive.y
        }

        return okX && okY
    }

    override fun next(): Point {
        val next = currentPoint
        currentPoint = Point(currentPoint.x + xDirection, currentPoint.y + yDirection)
        return next
    }

}

class PointProgression(
    override val start: Point,
    override val endInclusive: Point,
) : ClosedRange<Point>,
    Iterable<Point> {
    override fun iterator(): PointIterator = PointIterator(start, endInclusive)
}

enum class LineSegmentDirection {
    VERTICAL, HORIZONTAL, DIAGONAL
}