fun main() {
    val input = readInput("Day13")

    checkExample1()
    part1(input).println()

//    checkExample2()
//    part2(input).println()
}

private fun checkExample1() {
    val example = listOf(
        "#.##..##.",
        "..#.##.#.",
        "##......#",
        "##......#",
        "..#.##.#.",
        "..##..##.",
        "#.#.##.#.",
        "",
        "#...##..#",
        "#....#..#",
        "..##..###",
        "#####.##.",
        "#####.##.",
        "..##..###",
        "#....#..#",
    )
    val expected = 405
    check(part1(example) == expected)
}

private fun checkExample2() {

}

private fun part1(input: List<String>): Int {
    val spring = Day13Valley.parse(input)
    return spring.summarizedNotes().sum()
}

private fun part2(input: List<String>): Long {
    return 0
}

private class Day13Valley(val patterns: List<List<String>>) {
    companion object {
        fun parse(input: List<String>): Day13Valley {
            return input.fold(mutableListOf(mutableListOf<String>())) { list, str ->
                list.apply {
                    if (str.isBlank())
                        add(arrayListOf())
                    else
                        last().add(str)
                }
            }.let { Day13Valley(it) }
        }
    }

    fun summarizedNotes(): List<Int> {
        return patterns.map { pattern ->
            var summary = 0
            val horizontalReflectionCandidates = (0..<pattern.lastIndex).filter { pattern[it] == pattern[it + 1] }
            val horizontalReflections = horizontalReflectionCandidates.filter { base ->
                val range = if (base < pattern.size / 2) 0..base else 0..<pattern.lastIndex - base
                range.all { gap -> pattern[base + gap + 1] == pattern[base - gap] }
            }
            summary += horizontalReflections.sumOf { it + 1 } * 100

            val verticalReflectionCandidates = (0..<pattern.first().lastIndex).filter { column -> pattern.all { it[column] == it[column + 1] } }
            val verticalReflections = verticalReflectionCandidates.filter { base ->
                val range = if (base < pattern.first().length / 2) 0..base else 0..<pattern.first().lastIndex - base
                range.all { gap -> pattern.all { it[base + gap + 1] == it[base - gap] } }
            }
            summary += verticalReflections.sumOf { it + 1 }

            summary
        }
    }

}