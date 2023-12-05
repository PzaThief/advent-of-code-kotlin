fun main() {
    val input = readInput("Day05")

    checkExample1()
    part1(input).println()

//    checkExample2()
//    part2(input).println()
}

private fun checkExample1() {
    val example = listOf(
        "seeds: 79 14 55 13",
        "",
        "seed-to-soil map:",
        "50 98 2",
        "52 50 48",
        "",
        "soil-to-fertilizer map:",
        "0 15 37",
        "37 52 2",
        "39 0 15",
        "",
        "fertilizer-to-water map:",
        "49 53 8",
        "0 11 42",
        "42 0 7",
        "57 7 4",
        "",
        "water-to-light map:",
        "88 18 7",
        "18 25 70",
        "",
        "light-to-temperature map:",
        "45 77 23",
        "81 45 19",
        "68 64 13",
        "",
        "temperature-to-humidity map:",
        "0 69 1",
        "1 0 69",
        "",
        "humidity-to-location map:",
        "60 56 37",
        "56 93 4",
    )
    val expected = 35L
    check(part1(example) == expected)
}

private fun checkExample2() {

}

private fun part1(input: List<String>): Long {
    val almanac = Day05Almanac.parse(input)
    return almanac.mapTo("location").min()
}

private fun part2(input: List<String>): Int {
    return 0
}

private data class Day05Almanac(
    val seeds: List<Long>,
    val maps: List<AlmanacMap>,
) {
    companion object {
        const val SEEDS_PREFIX = "seeds: "
        const val MAP_DELIMITER = "map:"
        const val TO_DELIMITER = "-to-"
        const val START_MAP_STRING = "seed"
        fun parse(input: List<String>): Day05Almanac {
            val seeds = input.first().removePrefix(SEEDS_PREFIX).trim().split(' ').map { it.toLong() }
            val maps = mutableListOf<AlmanacMap>()
            for (str in input.drop(1)) {
                if (str.isEmpty()) {
                    continue
                }
                if (str.endsWith(MAP_DELIMITER)) {
                    maps.add(
                        AlmanacMap(
                            str.substringBefore(TO_DELIMITER),
                            str.substringAfter(TO_DELIMITER).substringBefore(' '),
                            mutableListOf()
                        )
                    )
                    continue
                }
                val (destinationStart, sourceStart, rangeLength) = str.split(' ').map { it.toLong() }
                maps.last().rule.add(
                    AlmanacMap.AlmanacMapRule(destinationStart, sourceStart, rangeLength)
                )
            }
            return Day05Almanac(seeds, maps)
        }
    }

    data class AlmanacMap(
        val from: String,
        val to: String,
        val rule: MutableList<AlmanacMapRule>
    ) {
        data class AlmanacMapRule(
            val destinationStart: Long,
            val sourceStart: Long,
            val rangeLength: Long
        )

        fun map(from: Long): Long {
            val rule = this.rule.find { from in it.sourceStart..(it.sourceStart + it.rangeLength) } ?: return from
            return from + rule.destinationStart - rule.sourceStart
        }
    }

    fun mapTo(destination: String): List<Long> {
        var flowValues = seeds
        var flowFrom = START_MAP_STRING
        while (flowFrom != destination) {
            val currentMap = maps.find { it.from == flowFrom }!!
            flowValues = flowValues.map { currentMap.map(it) }
            flowFrom = currentMap.to
        }
        return flowValues
    }
}