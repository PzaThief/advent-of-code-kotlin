import kotlin.math.abs

fun main() {
    val input = readInput("Day18")

    checkExample1()
    part1(input).println()

    checkExample2()
    part2(input).println()
}

private fun checkExample1() {
    val example = listOf(
        "R 6 (#70c710)",
        "D 5 (#0dc571)",
        "L 2 (#5713f0)",
        "D 2 (#d2c081)",
        "R 2 (#59c680)",
        "D 2 (#411b91)",
        "L 5 (#8ceee2)",
        "U 2 (#caa173)",
        "L 1 (#1b58a2)",
        "U 2 (#caa171)",
        "R 2 (#7807d2)",
        "U 3 (#a77fa3)",
        "L 2 (#015232)",
        "U 2 (#7a21e3)",
    )
    val expected = 62L
    check(part1(example) == expected)
}

private fun checkExample2() {
    val example = listOf(
        "R 6 (#70c710)",
        "D 5 (#0dc571)",
        "L 2 (#5713f0)",
        "D 2 (#d2c081)",
        "R 2 (#59c680)",
        "D 2 (#411b91)",
        "L 5 (#8ceee2)",
        "U 2 (#caa173)",
        "L 1 (#1b58a2)",
        "U 2 (#caa171)",
        "R 2 (#7807d2)",
        "U 3 (#a77fa3)",
        "L 2 (#015232)",
        "U 2 (#7a21e3)",
    )
    val expected = 952408144115L
    check(part2(example) == expected)
}

private fun part1(input: List<String>): Long {
    val lagoon = Day18Lagoon.parse(input)
    return lagoon.calculateLavaPool()
}

private fun part2(input: List<String>): Long {
    val lagoon = Day18Lagoon.parse(input)
    return lagoon.calculateLavaPool(true)
}

private class Day18Lagoon(val plans: List<Plan>) {
    enum class Direction {
        R, D, L, U;

        fun move(pair: Pair<Int, Int>, repeat: Int = 1) = when (this) {
            U -> Pair(pair.first - repeat, pair.second)
            L -> Pair(pair.first, pair.second - repeat)
            D -> Pair(pair.first + repeat, pair.second)
            R -> Pair(pair.first, pair.second + repeat)
        }
    }

    data class Plan(val direction: Direction, val meters: Int, val color: String) {
        fun route(changePlan: Boolean): Pair<Int, Direction> = if (changePlan) {
            Pair(color.take(5).toInt(16), Direction.entries[color.takeLast(1).toInt()])
        } else {
            Pair(meters, direction)
        }
    }

    companion object {
        fun parse(input: List<String>) = Day18Lagoon(input.map {
            val line = it.split(' ')
            Plan(Direction.valueOf(line[0]), line[1].toInt(), line[2].removeSurrounding("(#", ")"))
        })
    }

    fun calculateLavaPool(changePlan: Boolean = false): Long {
        var currentPosition = Pair(0, 0)
        var outLine = 0L
        var internalArea = 0L
        for (plan in plans) {
            val (meters, direction) = plan.route(changePlan)
            currentPosition = direction.move(currentPosition, meters).also { nextPosition ->
                outLine += meters
                internalArea += (nextPosition.second - currentPosition.second) * (nextPosition.first + currentPosition.first).toLong()
            }
        }

        return abs(internalArea / 2) + outLine / 2 + 1
    }
}