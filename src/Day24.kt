fun main() {
    val input = readInput("Day24")

    checkExample1()
    part1(input).println()

//    checkExample2()
//    part2(input).println()
}

private fun checkExample1() {
    val example = listOf(
        "19, 13, 30 @ -2,  1, -2",
        "18, 19, 22 @ -1, -1, -2",
        "20, 25, 34 @ -2, -2, -4",
        "12, 31, 28 @ -1, -2, -1",
        "20, 19, 15 @  1, -5, -3",
    )
    val expected = 2L
    check(Day24Air.parse(example).possibleCollidesIn2D(7, 27) == expected)
}

private fun checkExample2() {
}

private fun part1(input: List<String>): Long {
    val air = Day24Air.parse(input)
    return air.possibleCollidesIn2D(200000000000000L, 400000000000000L)
}

private fun part2(input: List<String>): Long {
    return 0
}

private class Day24Air(val hailstones: List<Hailstone>) {
    data class Line(val a: Double, val b: Double) { // ax + b
        fun findCross(other: Line): Point2D? {
            if (this.a == other.a) return null
            val x = (other.b - this.b) / (this.a - other.a)
            val y = this.a * x + this.b
            return Point2D(x, y)
        }
    }

    data class Point2D(val x: Double, val y: Double)
    data class Point3D(val x: Double, val y: Double, val z: Double)
    data class Velocity(val x: Int, val y: Int, val z: Int)
    data class Hailstone(val position: Point3D, val velocity: Velocity) {
        fun toLine() = Line(velocity.y.toDouble() / velocity.x, position.y - velocity.y * (position.x / velocity.x))
    }

    companion object {
        fun parse(input: List<String>): Day24Air {
            val hailstones = input.map {
                val (position, velocity) = it.split(" @ ")
                val (positionX, positionY, positionZ) = position.split(", ").map { it.trim().toDouble() }
                val (velocityX, velocityY, velocityZ) = velocity.split(", ").map { it.trim().toInt() }
                Hailstone(Point3D(positionX, positionY, positionZ), Velocity(velocityX, velocityY, velocityZ))
            }
            return Day24Air(hailstones)
        }
    }

    fun possibleCollidesIn2D(start: Long, end: Long): Long {
        val collides = mutableSetOf<Point2D>()
        val validRange = start.toDouble()..end.toDouble()
        for (i in hailstones.indices) {
            for (j in (i + 1)..hailstones.lastIndex) {
                val a = hailstones[i]
                val b = hailstones[j]
                val intersection = a.toLine().findCross(b.toLine()) ?: continue
                if (a.velocity.x > 0 && intersection.x < a.position.x) continue
                if (a.velocity.x < 0 && intersection.x > a.position.x) continue
                if (b.velocity.x > 0 && intersection.x < b.position.x) continue
                if (b.velocity.x < 0 && intersection.x > b.position.x) continue
                if (intersection.x in validRange && intersection.y in validRange) collides.add(intersection)
            }
        }
        return collides.size.toLong()
    }
}