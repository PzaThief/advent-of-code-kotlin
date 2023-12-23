import kotlin.math.abs

fun main() {
    val input = readInput("Day23")

    checkExample1()
    part1(input).println()

    checkExample2()
    part2(input).println()
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
    val expected = 154L
    check(part2(example) == expected)
}

private fun part1(input: List<String>): Long {
    val system = Day23Map(input)
    return system.getLongestWay().toLong()
}

private fun part2(input: List<String>): Long {
    val system = Day23Map(input)
    return system.getLongestWay(true).toLong()
}

private class Day23Map(val pattern: List<String>) {
    val hikeStart = 0 to pattern.first().indexOf('.')
    val hikeEnd = pattern.lastIndex to pattern.last().indexOf('.')
    val junctions = mutableMapOf(
        hikeStart to mutableListOf<Pair<Pair<Int, Int>, Int>>(),
        hikeEnd to mutableListOf(),
    )

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

    fun getLongestWay(useCrampon: Boolean = false): Int {
        for (row in pattern.indices) {
            for (col in pattern[row].indices) {
                if (pattern[row][col] == '.') {
                    val point = Pair(row, col)
                    if (validNearPoints(point, true).size > 2) {
                        junctions[point] = mutableListOf()
                    }
                }
            }
        }

        for (junction in junctions.keys) {
            var current = setOf(junction)
            val visited = mutableSetOf(junction)
            var distance = 0

            while (current.isNotEmpty()) {
                distance++
                current = buildSet {
                    for (c in current) {
                        validNearPoints(c, useCrampon).filter { it !in visited }.forEach { near ->
                            if (near in junctions) {
                                junctions.getValue(junction).add(near to distance)
                            } else {
                                add(near)
                                visited.add(near)
                            }
                        }
                    }
                }
            }
        }

        return largestStepFrom(current = hikeStart)
    }

    private fun validNearPoints(start: Pair<Int, Int>, useCrampon: Boolean): List<Pair<Int, Int>> {
        return Direction.entries
            .map { it to it.move(start) }
            .filter { (direction, next) ->
                if (next.first !in 0..pattern.lastIndex || next.second !in 0..pattern.last().lastIndex) return@filter false
                val char = pattern[next.first][next.second]
                val flag = Flag.from(char)
                if (flag == Flag.FOREST) return@filter false
                if (flag == Flag.SLOPE) {
                    if (!useCrampon && Direction.from(char)!!.isOpposite(direction)) return@filter false
                }
                return@filter true
            }.map { it.second }
    }

    private fun largestStepFrom(
        current: Pair<Int, Int>,
        path: Set<Pair<Int, Int>> = setOf(),
        baseDistance: Int = 0,
    ): Int {
        if (current == hikeEnd) return baseDistance

        val newPath = path.plus(current) as LinkedHashSet
        val max = junctions.getValue(current)
            .filter { (nextJunction) -> nextJunction !in newPath }
            .maxOfOrNull { (nextJunction, distance) ->
                largestStepFrom(nextJunction, newPath, baseDistance + distance)
            }

        return max ?: 0
    }
}