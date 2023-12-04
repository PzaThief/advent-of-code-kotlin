fun main() {
    val input = readInput("Day04")

    checkExample1()
    part1(input).println()

    checkExample2()
    part2(input).println()
}

private fun checkExample1() {
    val example = listOf(
        "Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53",
        "Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19",
        "Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1",
        "Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83",
        "Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36",
        "Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11"
    )
    val expected = 13
    check(part1(example) == expected)
}

private fun checkExample2() {
    val example = listOf(
        "Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53",
        "Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19",
        "Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1",
        "Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83",
        "Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36",
        "Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11"
    )
    val expected = 30
    check(part2(example) == expected)
}

private fun part1(input: List<String>): Int {
    return Day04Game.parse(input).sumOf { it.getPoint() }
}

private fun part2(input: List<String>): Int {
    var sum = 0
    val instanceByCard = mutableMapOf<Int, Int>()
    Day04Game.parse(input).onEachIndexed { index, game ->
        val instances = instanceByCard.getOrDefault(index, 0) + 1
        for (i in 1..game.matchedCount()) {
            instanceByCard.compute(index + i) { _, v -> if (v == null) instances else v + instances }
        }
        sum += instances
        instanceByCard.remove(index)
    }

    return sum
}

private data class Day04Game(
    val id: Int,
    val winningNumbers: Set<Int>,
    val havingNumbers: Set<Int>
) {
    companion object {
        const val GAME_PREFIX = "Card "
        const val GAME_ID_DELIMITER = ":"
        const val NUMBER_DELIMITER = "|"
        val ONE_OR_MORE_SPACE = "\\s+".toRegex()

        fun parse(input: List<String>): List<Day04Game> {
            return input.map { parse(it) }
        }

        fun parse(str: String): Day04Game {
            val id = str.removePrefix(GAME_PREFIX).substringBefore(GAME_ID_DELIMITER).trim().toInt()
            val winningNumbers =
                str.substringAfter(GAME_ID_DELIMITER).substringBefore(NUMBER_DELIMITER).trim().split(ONE_OR_MORE_SPACE)
                    .map { it.toInt() }
            val havingNumbers = str.substringAfter(NUMBER_DELIMITER).trim().split(ONE_OR_MORE_SPACE).map { it.toInt() }
            return Day04Game(id, winningNumbers.toSet(), havingNumbers.toSet())
        }
    }

    fun getPoint(): Int {
        val cnt = matchedCount()
        return if (cnt != 0) 1.shl(cnt - 1) else 0
    }

    fun matchedCount(): Int {
        return winningNumbers.intersect(havingNumbers).count()
    }
}
