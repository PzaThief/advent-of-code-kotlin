fun main() {
    val input = readInput("Day09")

    checkExample1()
    part1(input).println()

    checkExample2()
    part2(input).println()
}

private fun checkExample1() {
    val example = listOf(
        "0 3 6 9 12 15",
        "1 3 6 10 15 21",
        "10 13 16 21 30 45",
    )
    val expected = 114L
    check(part1(example) == expected)
}

private fun checkExample2() {
    val example = listOf(
        "0 3 6 9 12 15",
        "1 3 6 10 15 21",
        "10 13 16 21 30 45",
    )
    val expected = 2L
    check(part2(example) == expected)
}

private fun part1(input: List<String>): Long {
    return Day09Oasis.parse(input).nextNumbers().sum()
}

private fun part2(input: List<String>): Long {
    return Day09Oasis.parse(input).beforeNumbers().sum()
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

    fun beforeNumbers(): List<Long> {
        return reports.map { interpolate(it, -1) }
    }

    private fun interpolate(sequence: List<Long>, xi: Int): Long {
        var result = 0F
        for (i in sequence.indices) {
            var term = sequence[i].toFloat()
            for (j in sequence.indices) {
                if (j != i) {
                    term *= (xi - j) / (i - j).toFloat()
                }
            }
            result += term
        }
        return result.toLong()
    }

}