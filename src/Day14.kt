fun main() {
    val input = readInput("Day14")

    checkExample1()
    part1(input).println()

    checkExample2()
    part2(input).println()
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
    val expected = 64L
    check(part2(example) == expected)
}

private fun part1(input: List<String>): Long {
    val dish = Day14Dish(input.map { it.toList() })
    return dish.calculateLoad()
}

private fun part2(input: List<String>): Long {
    val dish = Day14Dish(input.map { it.toList() })
    return dish.calculateLoadWithSpin(1000000000)
}

private class Day14Dish(val grid: List<List<Char>>) {
    data class RockOnBase(val base: Int = 0, var count: Int = 0, var load: Int = 0) {
        fun addRock(gridSize: Int) {
            load += gridSize - base - count
            count++
        }
    }

    fun calculateLoad() = grid.rockOnBasesByColumns().sumOf { it.sumOf { it.load.toLong() } }
    fun calculateLoadWithSpin(cycles: Int) =
        grid.spinCycle(cycles).mapIndexed { index, chars ->
            chars.count { it == 'O' } * (grid.size - index).toLong()
        }.sum()

    private fun List<List<Char>>.rockOnBasesByColumns(): List<MutableList<RockOnBase>> {
        val grid = this
        return grid.first().indices.map { column ->
            val rocksByBase = grid.map { it[column] }.foldIndexed(mutableListOf(RockOnBase())) { index, list, rock ->
                when (rock) {
                    '#' -> list.add(RockOnBase(index + 1))
                    'O' -> list.last().addRock(grid.size)
                }
                list
            }
            rocksByBase
        }
    }

    private fun List<List<Char>>.spinCycle(cycles: Int): List<List<Char>> {
        val set = linkedSetOf(this)
        var circleStart = -1
        for (i in 0 until cycles) {
            val newGrid = set.last()
                .roll()
                .transpose().roll().transpose()
                .reversed().roll().reversed()
                .transpose().reversed().roll().reversed().transpose()
            if (newGrid in set) {
                circleStart = set.indexOf(newGrid)
                break
            }
            set.add(newGrid)
        }
        val idx = if (circleStart != -1) {
            (cycles - circleStart) % (set.size - circleStart) + circleStart
        } else {
            set.size - 1
        }
        return set.filterIndexed { index, _ -> index == idx }.first()
    }

    private fun (List<List<Char>>).roll(): List<List<Char>> {
        val columns = this.rockOnBasesByColumns().map {
            val newColumn = CharArray(this.size) { '.' }
            for (rockOnBase in it) {
                if (rockOnBase.base != 0) newColumn[rockOnBase.base - 1] = '#'
                for (i in 1..rockOnBase.count) {
                    newColumn[rockOnBase.base + i - 1] = 'O'
                }
            }
            newColumn.toList()
        }
        return columns.transpose()
    }

    private fun List<List<Char>>.transpose(): List<List<Char>> {
        return (this[0].indices).map { i -> (this.indices).map { j -> this[j][i] } }
    }

}