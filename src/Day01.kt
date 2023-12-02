fun main() {
    val input = readInput("Day01")

    checkExample1()
    part1(input).println()

    checkExample2()
    part2(input).println()
}

fun checkExample1() {
    val example = listOf(
        "1abc2",
        "pqr3stu8vwx",
        "a1b2c3d4e5f",
        "treb7uchet"
    )
    val expected = 142
    check(part1(example) == expected)
}

fun checkExample2() {
    val example = listOf(
        "two1nine",
        "eightwothree",
        "abcone2threexyz",
        "xtwone3four",
        "4nineeightseven2",
        "zoneight234",
        "7pqrstsixteen"
    )
    val expected = 281
    check(part2(example) == expected)
}

fun part1(input: List<String>): Int {
    return input.sumOf { str ->
        val firstDigit = str.find { it.isDigit() }
        val lastDigit = str.findLast { it.isDigit() }
        "$firstDigit$lastDigit".toInt()
    }
}

fun part2(input: List<String>): Int {
    return input.sumOf { str ->
        val firstDigit = findFirstDigit(str)
        val lastDigit = findFirstDigit(str, true)
        "$firstDigit$lastDigit".toInt()
    }
}

private fun findFirstDigit(
    str: String,
    reverse: Boolean = false
): Int {
    val digitByWord = mapOf(
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9
    )
    val range = if (!reverse) {
        str.indices
    } else {
        str.lastIndex downTo 0
    }

    for (i in range) {
        if (str[i].isDigit()) {
            return str[i].digitToInt()
        }
        for ((word, num) in digitByWord) {
            if (str.startsWith(word, i)) {
                return num
            }
        }
    }
    return 0
}
