fun main() {
    val input = readInput("Day07")

    checkExample1()
    part1(input).println()

    checkExample2()
    part2(input).println()
}

private fun checkExample1() {
    val example = listOf(
        "32T3K 765",
        "T55J5 684",
        "KK677 28",
        "KTJJT 220",
        "QQQJA 483",
    )
    val expected = 6440
    check(part1(example) == expected)
}

private fun checkExample2() {
    val example = listOf(
        "32T3K 765",
        "T55J5 684",
        "KK677 28",
        "KTJJT 220",
        "QQQJA 483",
    )
    val expected = 5905
    check(part2(example) == expected)
}

private fun part1(input: List<String>): Int {
    val game = Day07Game.parse(input)
    return game.getHandsRanks().mapIndexed { index, handWithType ->
        handWithType.first.bid * (index + 1)
    }.sum()
}

private fun part2(input: List<String>): Int {
    val game = Day07Game.parse(input)
    return game.getHandsRanks(true).mapIndexed { index, handWithType ->
        handWithType.first.bid * (index + 1)
    }.sum()
}

private data class Day07Game(
    val hands: List<Hand>
) {
    data class Hand(
        val card: String,
        val bid: Int
    ) {
        companion object {
            const val FIVE_CARD = 6
            const val FOUR_CARD = 5
            const val FULL_HOUSE = 4
            const val TRIPLE = 3
            const val TWO_PAIR = 2
            const val ONE_PAIR = 1
            const val HIGH_CARD = 0
            const val JOKER = 'J'
            val LABEL_ORDER_MAP = mapOf(
                '2' to 'A',
                '3' to 'B',
                '4' to 'C',
                '5' to 'D',
                '6' to 'E',
                '7' to 'F',
                '8' to 'G',
                '9' to 'H',
                'T' to 'I',
                'J' to 'J',
                'Q' to 'K',
                'K' to 'L',
                'A' to 'N',
            )

            fun parse(str: String): Hand {
                val (card, bid) = str.split(' ')
                return Hand(card, bid.toInt())
            }
        }

        fun getTypeAndPower(useJoker: Boolean = false): Pair<Int, String> {
            val charCountMap: Map<Char, Int>
            val powerString: String
            if (!useJoker) {
                charCountMap = card.groupingBy { it }.eachCount()
                powerString = card.map { LABEL_ORDER_MAP[it] }.toString()
            } else {
                val charCountMapWithoutJoker = card.filter { it != JOKER }.groupingBy { it }.eachCount()
                val newLabel = if (charCountMapWithoutJoker.isNotEmpty()) {
                    val maxCount = charCountMapWithoutJoker.maxOf { it.value }
                    charCountMapWithoutJoker.filter { it.value == maxCount }.maxOf { it.key }
                } else {
                    '0'
                }
                charCountMap = card.replace(JOKER, newLabel).groupingBy { it }.eachCount()
                powerString = card.map { if (it == JOKER) '0' else LABEL_ORDER_MAP[it] }.toString()
            }

            val type = when {
                charCountMap.count() == 1 -> FIVE_CARD
                charCountMap.count() == 2 && charCountMap.containsValue(4) -> FOUR_CARD
                charCountMap.count() == 2 && charCountMap.containsValue(3) -> FULL_HOUSE
                charCountMap.count() == 3 && charCountMap.containsValue(3) -> TRIPLE
                charCountMap.count { it.value == 2 } == 2 -> TWO_PAIR
                charCountMap.count() == 4 -> ONE_PAIR
                else -> HIGH_CARD
            }
            return Pair(type, powerString)
        }
    }

    companion object {
        fun parse(input: List<String>): Day07Game {
            return Day07Game(input.map { Hand.parse(it) })
        }
    }

    fun getHandsRanks(useJoker: Boolean = false): List<Pair<Hand, Pair<Int, String>>> {
        val handWithRank = mutableListOf<Pair<Hand, Pair<Int, String>>>()
        for (hand in hands) {
            handWithRank.add(
                Pair(hand, hand.getTypeAndPower(useJoker))
            )
        }
        return handWithRank.sortedBy { it.second.second }.sortedBy { it.second.first }
    }
}