package com.monta

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor

fun main() {
    val input = "day9.txt"
        .loadResource()
        .filterNotBlank()

    val map = Array(input.size) { index -> input[index].map { it.digitToInt() }.toIntArray() }
    println(findLocalMinimums(map))
    println(findCavernBasins(map))
}

private fun findLocalMinimums(map: Array<IntArray>): Long {
    val minimums = mutableListOf<Int>()
    for (y in map.indices) {
        for (x in map[y].indices) {
            val here = map[y][x]
            if (here == 9) continue // Can never be a minimum

            val up = map.getOrMax(x, y - 1)
            val down = map.getOrMax(x, y + 1)
            val left = map.getOrMax(x - 1, y)
            val right = map.getOrMax(x + 1, y)

            if (listOf(up, down, left, right).all { it > here }) minimums += here
        }
    }

    return minimums.sumOf { 1 + it.toLong() }
}

private fun findCavernBasins(map: Array<IntArray>): Long {
    val visited = mutableSetOf<Point>()
    val basins = mutableListOf<Long>()

    for (y in map.indices) {
        for (x in map[y].indices) {
            if (visited.contains(Point(x, y))) continue
            val here = map[y][x]
            if (here == 9) continue

            runBlocking {
                val basinActor = basinActor(visited)
                mapBasin(x, y, map, basinActor)

                val response = CompletableDeferred<Set<Point>>()
                basinActor.send(GetBasin(response))
                val result = response.await()
                visited += result
                basins += result.map { map[it.y][it.x] }.count().toLong()

                basinActor.close()
            }
        }
    }

    return basins.sorted().takeLast(3).reduce { acc, l -> acc * l }
}

private suspend fun mapBasin(x: Int, y: Int, map: Array<IntArray>, basin: SendChannel<BasinMsg>) {
    val here = map.getOrMax(x, y)
    if (here >= 9) return
    val contains = CompletableDeferred<Boolean>()
    basin.send(AddIfAbsentPoint(Point(x, y), contains))
    if (contains.await()) return

    coroutineScope {
        // up
        launch { mapBasin(x, y - 1, map, basin) }
        // down
        launch { mapBasin(x, y + 1, map, basin) }
        // left
        launch { mapBasin(x - 1, y, map, basin) }
        // right
        launch { mapBasin(x + 1, y, map, basin) }
    }
}

private fun Array<IntArray>.getOrMax(x: Int, y: Int): Int {
    if (y !in this.indices || x !in this[y].indices) return Int.MAX_VALUE
    return this[y][x]
}

sealed class BasinMsg
class AddIfAbsentPoint(val point: Point, val response: CompletableDeferred<Boolean>) : BasinMsg()
class GetBasin(val response: CompletableDeferred<Set<Point>>) : BasinMsg()

fun CoroutineScope.basinActor(exclude: Set<Point>) = actor<BasinMsg> {
    val set = mutableSetOf<Point>()
    for (msg in channel) {
        when (msg) {
            is AddIfAbsentPoint -> {
                val inSet = exclude.contains(msg.point) || set.contains(msg.point)
                if (!inSet) set.add(msg.point)
                msg.response.complete(inSet)
            }
            is GetBasin -> msg.response.complete(set)
        }
    }
}
