import java.util.*

fun main() {
    val input = readInput("Day17")

    checkExample1()
    part1(input).println()

    checkExample2()
    part2(input).println()
}

private fun checkExample1() {
    val example = listOf(
        "2413432311323",
        "3215453535623",
        "3255245654254",
        "3446585845452",
        "4546657867536",
        "1438598798454",
        "4457876987766",
        "3637877979653",
        "4654967986887",
        "4564679986453",
        "1224686865563",
        "2546548887735",
        "4322674655533",
    )
    val expected = 102
    check(part1(example) == expected)
}

private fun checkExample2() {
    val example = listOf(
        "2413432311323",
        "3215453535623",
        "3255245654254",
        "3446585845452",
        "4546657867536",
        "1438598798454",
        "4457876987766",
        "3637877979653",
        "4654967986887",
        "4564679986453",
        "1224686865563",
        "2546548887735",
        "4322674655533",
    )
    val expected = 94
    check(part2(example) == expected)
}

private fun part1(input: List<String>): Int {
    val city = Day17City(input.map { it.map { it.digitToInt() } })
    return city.leastHeatLoss()
}

private fun part2(input: List<String>): Int {
    val city = Day17City(input.map { it.map { it.digitToInt() } })
    return city.leastHeatLoss(minimumContinuation = 4, maximumContinuation = 10)
}

private class Day17City(val grid: List<List<Int>>) {
    enum class Direction {
        U, L, D, R;

        fun move(pair: Pair<Int, Int>) = when (this) {
            U -> Pair(pair.first - 1, pair.second)
            L -> Pair(pair.first, pair.second - 1)
            D -> Pair(pair.first + 1, pair.second)
            R -> Pair(pair.first, pair.second + 1)
        }

        fun turnRight() = Direction.entries[(this.ordinal - 1).mod(Direction.entries.size)]
        fun turnLeft() = Direction.entries[(this.ordinal + 1).mod(Direction.entries.size)]
    }

    data class State(val position: Pair<Int, Int>, val direction: Direction, val continuation: Int)

    fun leastHeatLoss(
        start: Pair<Int, Int> = Pair(0, 0),
        end: Pair<Int, Int> = Pair(grid.lastIndex, grid.last().lastIndex),
        minimumContinuation:Int = 0,
        maximumContinuation:Int = 3,
    ): Int {
        val visited = mutableSetOf<State>()
        val queue = PriorityQueue { t1: Pair<State, Int>, t2: Pair<State, Int> -> t1.second - t2.second }
        queue.add(Pair(State(start, Direction.R, 0), grid[start.first][start.second]))
        while (queue.peek().first.position != end) {
            val (state, accumulated) = queue.poll()
            if (!visited.add(state)) continue
            val directionCandidates = listOf(state.direction, state.direction.turnRight(), state.direction.turnLeft())

            directionCandidates.forEach { direction ->
                val next = direction.move(state.position)
                if (isPositionInGrid(next)) {
                    if (direction == state.direction && state.continuation < maximumContinuation) {
                        queue.add(Pair(State(next, direction, state.continuation + 1), accumulated + grid[next.first][next.second]))
                    }
                    if (direction != state.direction && state.continuation >= minimumContinuation) {
                        queue.add(Pair(State(next, direction, 1), accumulated + grid[next.first][next.second]))
                    }
                }
            }
        }

        return queue.peek().second - grid[start.first][start.second]
    }

    fun isPositionInGrid(pair: Pair<Int, Int>): Boolean {
        return (pair.first in grid.indices) && pair.second in grid.last().indices
    }

}