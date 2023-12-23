import java.util.*
import kotlin.math.abs

fun main() {
    val input = readInput("Day23")

    checkExample1()
    part1(input).println()

//    checkExample2()
//    part2(input).println()
}

private fun checkExample1() {
    val example = listOf(
        "#.#####################",
        "#.......#########...###",
        "#######.#########.#.###",
        "###.....#.>.>.###.#.###",
        "###v#####.#v#.###.#.###",
        "###.>...#.#.#.....#...#",
        "###v###.#.#.#########.#",
        "###...#.#.#.......#...#",
        "#####.#.#.#######.#.###",
        "#.....#.#.#.......#...#",
        "#.#####.#.#.#########v#",
        "#.#...#...#...###...>.#",
        "#.#.#v#######v###.###v#",
        "#...#.>.#...>.>.#.###.#",
        "#####v#.#.###v#.#.###.#",
        "#.....#...#...#.#.#...#",
        "#.#########.###.#.#.###",
        "#...###...#...#...#.###",
        "###.###.#.###v#####v###",
        "#...#...#.#.>.>.#.>.###",
        "#.###.###.#.###.#.#v###",
        "#.....###...###...#...#",
        "#####################.#",
    )
    val expected = 94L
    check(part1(example) == expected)
}

private fun checkExample2() {
}

private fun part1(input: List<String>): Long {
    val system = Day23Map(input)
    return system.getPossibleSteps().max().toLong()
}

private fun part2(input: List<String>): Long {
    return 0
}

private class Day23Map(val pattern: List<String>) {
    val start = 0 to pattern.first().indexOf('.')
    val end = pattern.lastIndex to pattern.last().indexOf('.')

    enum class Direction(val slope: Char) {
        U('^'), L('<'), D('v'), R('>');

        companion object {
            fun from(char: Char) = entries.firstOrNull { it.slope == char }
        }

        fun move(pair: Pair<Int, Int>) = when (this) {
            U -> Pair(pair.first - 1, pair.second)
            L -> Pair(pair.first, pair.second - 1)
            D -> Pair(pair.first + 1, pair.second)
            R -> Pair(pair.first, pair.second + 1)
        }

        fun isOpposite(other: Direction) = abs(this.ordinal - other.ordinal) == 2
    }

    enum class Flag {
        PATH, FOREST, SLOPE;

        companion object {
            fun from(char: Char) = when (char) {
                '.' -> PATH
                '#' -> FOREST
                else -> SLOPE
            }
        }
    }

    fun getPossibleSteps(): List<Int> {
        val steps = mutableListOf<Int>()
        val pathQueue = LinkedList<LinkedHashSet<Pair<Int, Int>>>()
        pathQueue.add(linkedSetOf(start))
        while (pathQueue.isNotEmpty()) {
            val currentPath = pathQueue.pop()
            val currentPoint = currentPath.last()
            if (currentPoint == end) {
                steps.add(currentPath.count() - 1)
                continue
            }
            val nextPoints = Direction.entries
                .map { it to it.move(currentPoint) }
                .filter { (direction, next) ->
                    if (next.first !in 0..pattern.lastIndex || next.second !in 0..pattern.last().lastIndex) return@filter false
                    if (next in currentPath) return@filter false
                    val char = pattern[next.first][next.second]
                    val flag = Flag.from(char)
                    if (flag == Flag.FOREST) return@filter false
                    if (flag == Flag.SLOPE) {
                        if (Direction.from(char)!!.isOpposite(direction)) return@filter false
                    }
                    return@filter true
                }
            pathQueue.addAll(nextPoints.map { currentPath.plus(it.second) as LinkedHashSet })
        }

        return steps.also { println(it) }
    }

}