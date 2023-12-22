fun main() {
    val input = readInput("Day22")

    checkExample1()
    part1(input).println()

    checkExample2()
    part2(input).println()
}

private fun checkExample1() {
    val example = listOf(
        "1,0,1~1,2,1",
        "0,0,2~2,0,2",
        "0,2,3~2,2,3",
        "0,0,4~0,2,4",
        "2,0,5~2,2,5",
        "0,1,6~2,1,6",
        "1,1,8~1,1,9",
    )
    val expected = 5L
    check(part1(example) == expected)
}

private fun checkExample2() {
    val example = listOf(
        "1,0,1~1,2,1",
        "0,0,2~2,0,2",
        "0,2,3~2,2,3",
        "0,0,4~0,2,4",
        "2,0,5~2,2,5",
        "0,1,6~2,1,6",
        "1,1,8~1,1,9",
    )
    val expected = 7L
    check(part2(example) == expected)
}

private fun part1(input: List<String>): Long {
    val system = Day22Stack.parse(input)
    return system.countRemovable()
}

private fun part2(input: List<String>): Long {
    val system = Day22Stack.parse(input)
    return system.countRemovableEffects()
}

private class Day22Stack(val bricks: List<List<Point3D>>) {
    data class Point3D(val x: Int, val y: Int, val z: Int)
    companion object {
        fun parse(input: List<String>): Day22Stack {
            val bricks = input.map { l ->
                val (a, b) = l.split("~").map { p ->
                    val (x, y, z) = p.split(",").map(String::toInt)
                    Point3D(x, y, z)
                }

                buildList {
                    for (x in a.x..b.x) {
                        for (y in a.y..b.y) {
                            for (z in a.z..b.z) {
                                add(Point3D(x, y, z))
                            }
                        }
                    }
                }
            }.sortedBy { b -> b.minOf { it.z } }
            return Day22Stack(bricks)
        }
    }

    fun workGravity(bricks: List<List<Point3D>>): Pair<List<List<Point3D>>, Int> {
        val new = mutableListOf<List<Point3D>>()
        val fallen = hashSetOf<Point3D>()
        var move = 0

        for (brick in bricks) {
            var current = brick
            while (true) {
                val falling = current.map { p -> Point3D(p.x, p.y, p.z - 1) }
                if (falling.any { it.z <= 0 || it in fallen }) {
                    new += current
                    fallen += current
                    if (current != brick) move++
                    break
                }

                current = falling
            }
        }
        return new to move
    }

    fun countRemovable(): Long {
        val fallenBricks = workGravity(bricks).first
        val moves = fallenBricks.mapIndexed { index, _ ->
            workGravity(fallenBricks.filterIndexed { i, _ -> i != index }).second
        }
        return moves.count { it == 0 }.toLong()
    }

    fun countRemovableEffects(): Long {
        val fallenBricks = workGravity(bricks).first
        val moves = fallenBricks.mapIndexed { index, _ ->
            workGravity(fallenBricks.filterIndexed { i, _ -> i != index }).second
        }
        return moves.sum().toLong()
    }

}