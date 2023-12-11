import kotlin.math.abs
fun main() {
    val input = readInput("Day11")

    checkExample1()
    part1(input).println()

    checkExample2()
    part2(input).println()
}

private fun checkExample1() {
    val example = listOf(
        "...#......",
        ".......#..",
        "#.........",
        "..........",
        "......#...",
        ".#........",
        ".........#",
        "..........",
        ".......#..",
        "#...#.....",
    )
    val expected = 374L
    check(part1(example) == expected)
}

private fun checkExample2() {
    val example = listOf(
        "...#......",
        ".......#..",
        "#.........",
        "..........",
        "......#...",
        ".#........",
        ".........#",
        "..........",
        ".......#..",
        "#...#.....",
    )
    val expected = 8410L
    check(Day11Universe(example.map { it.toList() }).distances(100).sum() == expected)
}

private fun part1(input: List<String>): Long {
    val universe = Day11Universe(input.map { it.toList() })
    return universe.distances(2).sum()
}

private fun part2(input: List<String>): Long {
    val universe = Day11Universe(input.map { it.toList() })
    return universe.distances(1000000).sum()
}

private class Day11Universe(val grid: List<List<Char>>) {
    private val emptyRows: List<Int> = grid.indices.filter { '#' !in grid[it] }
    private val emptyCols: List<Int> = grid.first().indices.filter { col -> grid.all { it.getOrNull(col) != '#' } }
    private val galaxies: List<Pair<Int, Int>> = buildList {
        for ((y, row) in grid.withIndex()) {
            for ((x, char) in row.withIndex()) {
                if (char == '#') add(Pair(y, x))
            }
        }
    }

    fun distances(factor: Long): List<Long> = galaxies.withIndex().flatMap { (i, galaxy) ->
        val (y0, x0) = galaxy
        galaxies.subList(i + 1, galaxies.size).map { (y1, x1) ->
            val emptyRowsBetween = abs(emptyRows.binarySearch { it - y0 } - emptyRows.binarySearch { it - y1 })
            val emptyColsBetween = abs(emptyCols.binarySearch { it - x0 } - emptyCols.binarySearch { it - x1 })
            val expendEffect = (factor - 1) * (emptyRowsBetween + emptyColsBetween)
            abs(y1 - y0) + abs(x1 - x0) + expendEffect
        }
    }

}