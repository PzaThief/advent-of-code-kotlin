import com.microsoft.z3.*
import kotlin.math.pow
import kotlin.math.roundToLong

fun main() {
    val input = readInput("Day24")

    checkExample1()
    part1(input).println()

    checkExample2()
    part2(input).println()
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
    val example = listOf(
        "19, 13, 30 @ -2,  1, -2",
        "18, 19, 22 @ -1, -1, -2",
        "20, 25, 34 @ -2, -2, -4",
        "12, 31, 28 @ -1, -2, -1",
        "20, 19, 15 @  1, -5, -3",
    )
    val expected = 47L
    check(Day24Air.parse(example).findOptimalThrow() == expected)
}

private fun part1(input: List<String>): Long {
    val air = Day24Air.parse(input)
    return air.possibleCollidesIn2D(200000000000000L, 400000000000000L)
}

private fun part2(input: List<String>): Long {
    val air = Day24Air.parse(input)
    return air.findOptimalThrow()
}

private class Day24Air(val hailstones: List<Hailstone>) {
    data class Line(val a: Double, val b: Double) { // ax + b
        fun findCross(other: Line): Point2D? {
            if (this.a == other.a) return null
            val x = (other.b - this.b) / (this.a - other.a)
            val y = this.a * x + this.b
            return Point2D(x.roundTo(1), y.roundTo(1))
        }
    }

    data class Point2D(val x: Double, val y: Double)
    data class Point3D(val x: Double, val y: Double, val z: Double)
    data class Velocity(val x: Int, val y: Int, val z: Int)
    data class Hailstone(val position: Point3D, val velocity: Velocity) {
        fun findCross(other: Hailstone): Point2D? {
            if (this.velocity.x == 0) {
                val x = this.position.x
                val y = other.slope() * (this.position.x - other.position.x) + other.position.y
                return Point2D(x, y.roundTo(1))
            }
            if (other.velocity.x == 0) {
                val x = other.position.x
                val y = this.slope() * (other.position.x - this.position.x) + this.position.y
                return Point2D(x, y.roundTo(1))
            }
            return this.toLine().findCross(other.toLine())
        }

        fun slope() = velocity.y.toDouble() / velocity.x
        fun toLine() = Line(slope(), position.y - velocity.y * (position.x / velocity.x))
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

    private fun getValidCross(a: Hailstone, b: Hailstone, range: ClosedRange<Double>? = null): Point2D? {
        val intersection = a.findCross(b) ?: return null
        if (a.velocity.x > 0 && intersection.x < a.position.x) return null
        if (a.velocity.x < 0 && intersection.x > a.position.x) return null
        if (b.velocity.x > 0 && intersection.x < b.position.x) return null
        if (b.velocity.x < 0 && intersection.x > b.position.x) return null
        if (range != null && (intersection.x !in range || intersection.y !in range)) return null
        return intersection
    }

    fun possibleCollidesIn2D(start: Long, end: Long): Long {
        val collides = mutableSetOf<Point2D>()
        val validRange = start.toDouble()..end.toDouble()
        for (i in hailstones.indices) {
            for (j in (i + 1)..hailstones.lastIndex) {
                val a = hailstones[i]
                val b = hailstones[j]
                val intersection = getValidCross(a, b, validRange)
                if (intersection != null) collides.add(intersection)
            }
        }
        return collides.size.toLong()
    }

    fun findOptimalThrow(): Long {
        val ctx = Context() // if using proof = true, Real errors
        val solver = ctx.mkSolver()

        val (positionX, positionY, positionZ) = listOf("x", "y", "z").map { ctx.mkRealConst(it) }
        val (velocityX, velocityY, velocityZ) = listOf("vx", "vy", "vz").map { ctx.mkRealConst(it) }
        hailstones.take(3).forEachIndexed { index, hailstone ->
            val time = ctx.mkRealConst("t$index")
            solver.add(
                ctx.mkEq(
                    ctx.mkAdd(positionX, ctx.mkMul(velocityX, time)),
                    ctx.mkAdd(ctx.mkReal(hailstone.position.x.toLong()), ctx.mkMul(ctx.mkReal(hailstone.velocity.x.toLong()), time))
                )
            )
            solver.add(
                ctx.mkEq(
                    ctx.mkAdd(positionY, ctx.mkMul(velocityY, time)),
                    ctx.mkAdd(ctx.mkReal(hailstone.position.y.toLong()), ctx.mkMul(ctx.mkReal(hailstone.velocity.y.toLong()), time))
                )
            )
            solver.add(
                ctx.mkEq(
                    ctx.mkAdd(positionZ, ctx.mkMul(velocityZ, time)),
                    ctx.mkAdd(ctx.mkReal(hailstone.position.z.toLong()), ctx.mkMul(ctx.mkReal(hailstone.velocity.z.toLong()), time))
                )
            )
        }
        if (solver.check() == Status.SATISFIABLE) {
            return solver.model.eval(ctx.mkAdd(positionX, ctx.mkAdd(positionY, positionZ)), false).toString().toLong()
        }
        return 0
    }
}

fun Double.roundTo(numFractionDigits: Int): Double {
    if (this.isNaN()) return this
    val factor = 10.0.pow(numFractionDigits.toDouble())
    return (this * factor).roundToLong() / factor
}
