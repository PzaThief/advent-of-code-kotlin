import kotlin.math.absoluteValue

fun main() {
    val input = readInput("Day10")

    checkExample1()
    part1(input).println()

    checkExample2()
    part2(input).println()
}

private fun checkExample1() {
    val example = listOf(
        ".....",
        ".S-7.",
        ".|.|.",
        ".L-J.",
        ".....",
    )
    val expected = 4
    check(part1(example) == expected)
}

private fun checkExample2() {
    val example = listOf(
        "FF7FSF7F7F7F7F7F---7",
        "L|LJ||||||||||||F--J",
        "FL-7LJLJ||||||LJL-77",
        "F--JF--7||LJLJIF7FJ-",
        "L---JF-JLJIIIIFJLJJ7",
        "|F|F-JF---7IIIL7L|7|",
        "|FFJF7L7F-JF7IIL---7",
        "7-L-JL7||F7|L7F-7F7|",
        "L.L7LFJ|||||FJL7||LJ",
        "L7JLJL-JLJLJL--JLJ.L",
    )
    val expected = 10
    check(part2(example) == expected)
}

private fun part1(input: List<String>): Int {
    val grid = Day10Grid(input.map { it.toList() })
    return grid.findPath().size / 2
}

private fun part2(input: List<String>): Int {
    val grid = Day10Grid(input.map { it.toList() })
    val path = grid.findPath()
    val loopSize = 1 + (path.asSequence().plus(path[0]).zipWithNext { (y0, x0), (y1, x1) ->
        x0 * y1 - x1 * y0
    }.sum().absoluteValue - path.size) / 2

    return loopSize
}

private class Day10Grid(val grid: List<List<Char>>) {
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
            Pair(Direction.U, '|') to Direction.U,
            Pair(Direction.U, '7') to Direction.L,
            Pair(Direction.U, 'F') to Direction.R,
            Pair(Direction.L, '-') to Direction.L,
            Pair(Direction.L, 'F') to Direction.D,
            Pair(Direction.L, 'L') to Direction.U,
            Pair(Direction.D, '|') to Direction.D,
            Pair(Direction.D, 'L') to Direction.R,
            Pair(Direction.D, 'J') to Direction.L,
            Pair(Direction.R, '-') to Direction.R,
            Pair(Direction.R, 'J') to Direction.U,
            Pair(Direction.R, '7') to Direction.D,
        )
    }

    fun findPath(): List<Pair<Int, Int>> {
        var path: List<Pair<Int, Int>> = emptyList()
        var startPos = Pair(-1, -1)
        for ((i, line) in grid.withIndex()) {
            for ((j, char) in line.withIndex()) {
                if (char != 'S') continue
                startPos = Pair(i, j)
            }
        }
        for (startDir in Direction.entries) {
            val candidatePath = mutableListOf<Pair<Int, Int>>()
            var pos = startPos
            var dir: Direction? = startDir
            while (dir != null) {
                candidatePath.add(pos)
                pos = dir.move(pos)
                dir = routeMap[Pair(dir, grid.getOrNull(pos.first)?.getOrNull(pos.second))]
            }
            if (pos == startPos) {
                path = candidatePath
                break
            }
        }
        return path
    }
}