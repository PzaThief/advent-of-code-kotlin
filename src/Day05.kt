import kotlin.math.max
import kotlin.math.min

fun main() {
    val input = readInput("Day05")

    checkExample1()
    part1(input).println()

    checkExample2()
    part2(input).println()
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
    val expected = 46L
    check(part2(example) == expected)
}

private fun part1(input: List<String>): Long {
    val almanac = Day05Almanac.parsePart1(input)
    return almanac.mapTo("location").min()
}

private fun part2(input: List<String>): Long {
    val almanac = Day05Almanac.parsePart2(input)
    return almanac.mapRangeTo("location").minOf { it.first }
}

private data class Day05Almanac(
    val seeds: List<Long>? = null,
    val seedRanges: List<Pair<Long, Long>>? = null,
    val maps: List<AlmanacMap>,
) {
    companion object {
        const val SEEDS_PREFIX = "seeds: "
        const val MAP_DELIMITER = "map:"
        const val TO_DELIMITER = "-to-"
        const val START_MAP_STRING = "seed"
        fun parsePart1(input: List<String>): Day05Almanac {
            val seeds = input.first().removePrefix(SEEDS_PREFIX).trim().split(' ')
                .map { it.toLong() }
            val maps = parseMap(input.drop(1))
            return Day05Almanac(seeds = seeds, maps = maps)
        }

        fun parsePart2(input: List<String>): Day05Almanac {
            val seedRanges = input.first().removePrefix(SEEDS_PREFIX).trim().split(' ')
                .chunked(2) { Pair(it[0].toLong(), it[0].toLong() + it[1].toLong()) }
            val maps = parseMap(input.drop(1))
            return Day05Almanac(seedRanges = seedRanges, maps = maps)
        }

        private fun parseMap(input: List<String>): MutableList<AlmanacMap> {
            val maps = mutableListOf<AlmanacMap>()
            for (str in input) {
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
            return maps
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
        ) {
            val sourceEnd = sourceStart + rangeLength
            val gap = destinationStart - sourceStart
        }

        fun map(from: Long): Long {
            val rule = findAvailableRules(from, from).firstOrNull() ?: return from
            return from + rule.gap
        }

        fun mapRange(start: Long, end: Long): List<Pair<Long, Long>> {
            val rules = findAvailableRules(start, end).sortedBy { it.sourceStart }
            if (rules.isEmpty()) {
                return listOf(Pair(start, end))
            }
            val unmappedRanges = mutableListOf<Pair<Long, Long>>()
            val mappedRanges = mutableListOf<Pair<Long, Long>>()

            val leftExclusiveRange = start until rules.first().sourceStart
            if (!leftExclusiveRange.isEmpty()) unmappedRanges.add(Pair(leftExclusiveRange.first, leftExclusiveRange.last))

            for ((index, rule) in rules.withIndex()) {
                val intersectRange = max(start, rule.sourceStart)..min(end, (rule.sourceEnd))
                mappedRanges.add(Pair(intersectRange.first + rule.gap, intersectRange.last + rule.gap))

                if (index > 0) {
                    val gapRange = rules[index-1].sourceEnd+ 1 until rule.sourceStart
                    if (!gapRange.isEmpty()) {
                        unmappedRanges.add(Pair(gapRange.first, gapRange.last))
                    }
                }
            }

            val rightExclusiveRange = rules.last().sourceEnd + 1..end
            if (!rightExclusiveRange.isEmpty()) unmappedRanges.add(Pair(rightExclusiveRange.first, rightExclusiveRange.last))

            return unmappedRanges + mappedRanges
        }

        fun findAvailableRules(start: Long, end: Long): List<AlmanacMapRule> {
            return this.rule.filter { max(start, it.sourceStart) <= min(end, it.sourceEnd) }
        }
    }

    fun mapTo(destination: String): List<Long> {
        var flowValues = seeds!!
        var flowFrom = START_MAP_STRING
        while (flowFrom != destination) {
            val currentMap = maps.find { it.from == flowFrom }!!
            flowValues = flowValues.map { currentMap.map(it) }
            flowFrom = currentMap.to
        }
        return flowValues
    }

    fun mapRangeTo(destination: String): List<Pair<Long, Long>> {
        var flowRanges = seedRanges!!
        var flowFrom = START_MAP_STRING
        while (flowFrom != destination) {
            val currentMap = maps.find { it.from == flowFrom }!!
            flowRanges = flowRanges.map { currentMap.mapRange(it.first, it.second) }.flatten()
            flowFrom = currentMap.to
        }
        return flowRanges
    }

}