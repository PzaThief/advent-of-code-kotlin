fun main() {
    val input = readInput("Day16")

    checkExample1()
    part1(input).println()

//    checkExample2()
//    part2(input).println()
}

private fun checkExample1() {
    val example = listOf(
        ".|...\\....",
        "|.-.\\.....",
        ".....|-...",
        "........|.",
        "..........",
        ".........\\",
        "..../.\\\\..",
        ".-.-/..|..",
        ".|....-|.\\",
        "..//.|....",
    )
    val expected = 46
    check(part1(example) == expected)
}

private fun checkExample2() {
}

private fun part1(input: List<String>): Int {
    val floor = Day16Floor(input)
    return floor.calculateEnergizedTiles()
}

private fun part2(input: List<String>): Int {
    return 0
}

private class Day16Floor(val grid: List<String>) {
    enum class Direction {
        U, L, D, R;

        fun move(pair: Pair<Int, Int>) = when (this) {
            U -> Pair(pair.first - 1, pair.second)
            L -> Pair(pair.first, pair.second - 1)
            D -> Pair(pair.first + 1, pair.second)
            R -> Pair(pair.first, pair.second + 1)
        }
    }

    companion object {
        private val routeMap = mapOf(
            Direction.U to '/' to listOf(Direction.R),
            Direction.U to '\\' to listOf(Direction.L),
            Direction.U to '-' to listOf(Direction.L, Direction.R),
            Direction.L to '/' to listOf(Direction.D),
            Direction.L to '\\' to listOf(Direction.U),
            Direction.L to '|' to listOf(Direction.U, Direction.D),
            Direction.D to '/' to listOf(Direction.L),
            Direction.D to '\\' to listOf(Direction.R),
            Direction.D to '-' to listOf(Direction.L, Direction.R),
            Direction.R to '/' to listOf(Direction.U),
            Direction.R to '\\' to listOf(Direction.D),
            Direction.R to '|' to listOf(Direction.U, Direction.D),
        )
    }

    fun calculateEnergizedTiles(initialPoint: Pair<Int, Int> = Pair(0, 0), initialDirection: Direction = Direction.R): Int {
        val stack = mutableListOf(initialPoint to initialDirection)
        val visited = stack.toMutableSet()
        while (true) {
            val (point, direction) = stack.removeLastOrNull() ?: break
            (routeMap[direction to grid[point.first][point.second]] ?: listOf(direction)).forEach { nextDirection ->
                val nextPoint = nextDirection.move(point)
                if (nextPoint.first in grid.indices && nextPoint.second in grid[nextPoint.first].indices) {
                    val next = nextPoint to nextDirection
                    visited.add(next) && stack.add(next)
                }
            }
        }
        return visited.distinctBy { it.first }.size
    }

}