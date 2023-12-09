import java.math.BigInteger

fun main() {
    val input = readInput("Day09")

    checkExample1()
    part1(input).println()

//    checkExample2()
//    part2(input).println()
}

private fun checkExample1() {
    val example = listOf(
        "0 3 6 9 12 15",
        "1 3 6 10 15 21",
        "10 13 16 21 30 45",
    )
    val expected = 114L
    check(part1(example).also { println(it) } == expected)
}

private fun checkExample2() {
}

private fun part1(input: List<String>): Long {
    return Day09Oasis.parse(input).nextNumbers().sum()
}

private fun part2(input: List<String>): Long {
    return 0
}

private data class Day09Oasis(
    val reports: List<List<Long>>
) {
    companion object {
        fun parse(input: List<String>): Day09Oasis {
            return Day09Oasis(input.map { it.split(' ').map { it.toLong() } })
        }
    }
    fun nextNumbers(): List<Long> {
        return reports.map { interpolate(it, it.size) }
    }

    private fun interpolate(sequence: List<Long>, xi: Int): Long {
        var result = BigInteger.ZERO

        for (i in sequence.indices) {
            var num = BigInteger.ONE
            var den = BigInteger.ONE
            for (j in sequence.indices) {
                if (j != i) {
                    num *= (xi - j).toBigInteger()
                    den *= (i - j).toBigInteger()
                }
            }
            result += num / den * sequence[i].toBigInteger()
        }

        return result.toLong()
    }

}