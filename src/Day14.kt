fun main() {
    val input = readInput("Day14")

    checkExample1()
    part1(input).println()

//    checkExample2()
//    part2(input).println()
}

private fun checkExample1() {
    val example = listOf(
        "O....#....",
        "O.OO#....#",
        ".....##...",
        "OO.#O....O",
        ".O.....O#.",
        "O.#..O.#.#",
        "..O..#O..O",
        ".......O..",
        "#....###..",
        "#OO..#....",
    )
    val expected = 136L
    check(part1(example) == expected)
}

private fun checkExample2() {

}

private fun part1(input: List<String>): Long {
    val dish = Day14Dish(input)
    return dish.northLoad()
}

private fun part2(input: List<String>): Long {
    return 0
}

private class Day14Dish(val grid: List<String>) {
    fun northLoad(): Long {
        return grid.first().indices.sumOf { column ->
            val rocksByBase = grid.map { it[column] }.foldIndexed(mutableListOf(Pair(0, 0))) { index, list, rock ->
                list.apply {
                    when (rock) {
                        '#' -> add(Pair(index + 1, 0))
                        'O' -> set(lastIndex, Pair(last().first, last().second + 1))
                    }
                }
            }
            rocksByBase.sumOf { baseWithRocks ->
                (0..<baseWithRocks.second).sumOf { grid.size - baseWithRocks.first - it  }.toLong()
            }
        }
    }
}