fun main() {
    val input = readInput("Day06")

    checkExample1()
    part1(input).println()

    checkExample2()
    part2(input).println()
}

private fun checkExample1() {
    val example = listOf(
        "Time:      7  15   30",
        "Distance:  9  40  200"
    )
    val expected = 288
    check(part1(example) == expected)
}

private fun checkExample2() {
    val example = listOf(
        "Time:      7  15   30",
        "Distance:  9  40  200"
    )
    val expected = 71503L
    check(part2(example) == expected)
}

private val ONE_OR_MORE_SPACE = "\\s+".toRegex()
private fun part1(input: List<String>): Int {
    val times = input[0].substringAfter(":").trim().split(ONE_OR_MORE_SPACE)
    val distances = input[1].substringAfter(":").trim().split(ONE_OR_MORE_SPACE)
    val races = times.mapIndexed { i, v -> Day06Race(v.toInt(), distances[i].toInt()) }
    return races.map { race ->
        val minimumHold = findFirstTrueByBinarySearch(1, race.time / 2) {
            it * (race.time - it) > race.distance
        }
        race.time - (minimumHold * 2) + 1
    }.reduce(Int::times)
}

private fun part2(input: List<String>): Long {
    val time = input[0].substringAfter(":").replace(ONE_OR_MORE_SPACE, "").toLong()
    val distance = input[1].substringAfter(":").replace(ONE_OR_MORE_SPACE, "").toLong()
    val minimumHold = findFirstTrueByBinarySearch(1, time / 2) {
        it * (time - it) > distance
    }
    return time - (minimumHold * 2) + 1
}

private data class Day06Race(
    val time: Int,
    val distance: Int,
)

private fun findFirstTrueByBinarySearch(start: Long, end: Long, function: (v: Long) -> Boolean): Long {
    var low = start
    var high = end
    var mid: Long
    while (low <= high) {
        mid = low + ((high - low) / 2)
        if (function(mid)) {
            high = mid - 1
        } else {
            low = mid + 1
        }
    }
    return low
}

private fun findFirstTrueByBinarySearch(start: Int, end: Int, function: (v: Int) -> Boolean): Int {
    return findFirstTrueByBinarySearch(start.toLong(), end.toLong()) { v -> function(v.toInt()) }.toInt()
}