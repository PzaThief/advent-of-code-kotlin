import java.util.SortedSet

fun main() {
    val input = readInput("Day18")

    checkExample1()
    part1(input).println()

//    checkExample2()
//    part2(input).println()
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
    val expected = 62
    check(part1(example) == expected)
}

private fun checkExample2() {
}

private fun part1(input: List<String>): Int {
    val lagoon = Day18Lagoon.parse(input)
    return lagoon.calculateLavaPool()
}

private fun part2(input: List<String>): Int {
    return 0
}

private class Day18Lagoon(val plans: List<Plan>) {
    enum class Direction {
        U, L, D, R;

        fun move(pair: Pair<Int, Int>) = when (this) {
            U -> Pair(pair.first - 1, pair.second)
            L -> Pair(pair.first, pair.second - 1)
            D -> Pair(pair.first + 1, pair.second)
            R -> Pair(pair.first, pair.second + 1)
        }
    }

    data class Plan(val direction: Direction, val meters: Int, val color: String)

    companion object {
        fun parse(input: List<String>) = Day18Lagoon(input.map {
            val line = it.split(' ')
            Plan(Direction.valueOf(line[0]), line[1].toInt(), line[2])
        })
    }

    fun calculateLavaPool(): Int {
        val trenchByLine = sortedMapOf<Int, SortedSet<Int>>()
        var currentPosition = Pair(0, 0)
        trenchByLine.getOrPut(currentPosition.first) { sortedSetOf() }.add(currentPosition.second)
        for (plan in plans) {
            for (meter in 1..plan.meters) {
                currentPosition = plan.direction.move(currentPosition)
                trenchByLine.getOrPut(currentPosition.first) { sortedSetOf() }.add(currentPosition.second)
            }
        }

        var lavaPool = 0
        val leftTop = Pair(trenchByLine.firstKey(), trenchByLine[trenchByLine.firstKey()]!!.first())
        val queue = mutableListOf(Pair(leftTop.first + 1, leftTop.second + 1))
        val visited = mutableSetOf(queue.first())
        while (queue.isNotEmpty()) {
            val interior = queue.removeFirst()
            lavaPool++
            Direction.entries.forEach {
                val near = it.move(interior)
                if (near.second !in trenchByLine[near.first]!!) visited.add(near) && queue.add(near)
            }
        }
        lavaPool += trenchByLine.values.sumOf { it.count() }
        return lavaPool
    }
}