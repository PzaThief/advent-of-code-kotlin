fun main() {
    val input = readInput("Day12")

    checkExample1()
    part1(input).println()

    checkExample2()
    part2(input).println()
}

private fun checkExample1() {
    val example = listOf(
        "???.### 1,1,3",
        ".??..??...?##. 1,1,3",
        "?#?#?#?#?#?#?#? 1,3,1,6",
        "????.#...#... 4,1,1",
        "????.######..#####. 1,6,5",
        "?###???????? 3,2,1",
    )
    val expected = 21L
    check(part1(example) == expected)
}

private fun checkExample2() {
    val example = listOf(
        "???.### 1,1,3",
        ".??..??...?##. 1,1,3",
        "?#?#?#?#?#?#?#? 1,3,1,6",
        "????.#...#... 4,1,1",
        "????.######..#####. 1,6,5",
        "?###???????? 3,2,1",
    )
    val expected = 525152L
    check(part2(example) == expected)
}

private fun part1(input: List<String>): Long {
    val spring = Day12Spring(input)
    return spring.countPossibleWays().sum()
}

private fun part2(input: List<String>): Long {
    val spring = Day12Spring(input)
    return spring.countPossibleWays(5).sum()
}

private class Day12Spring(val springs: List<String>) {
    val countMemory: MutableMap<Int, MutableMap<List<Int>, Long>> = mutableMapOf()
    fun countPossibleWays(repeat: Int = 1): List<Long> {
        return springs.mapIndexed { index, s ->
            var (condition, damaged) = s.split(' ')
            condition = List(repeat) { condition }.joinToString("?")
            damaged = List(repeat) { damaged }.joinToString(",")
            countPossibleWay(index, condition, damaged.split(',').map { it.toInt() })
        }
    }

    private fun countPossibleWay(
        index: Int, condition: String, damages: List<Int>, current: Int = 0, damagePosition: Int = 0, currentDamage: Int = 0
    ): Long {
        if (current >= condition.length) return if (damagePosition >= damages.size || (damagePosition == damages.lastIndex && currentDamage == damages[damagePosition])) 1 else 0
        val counted = countMemory.getOrPut(index) { mutableMapOf() }[listOf(current, damagePosition, currentDamage)]
        if (counted != null) return counted
        var possibleWays = 0L
        if (condition[current] != '#') {
            if (currentDamage > 0 && damages[damagePosition] == currentDamage) {
                possibleWays += countPossibleWay(index, condition, damages, current + 1, damagePosition + 1, 0)
            } else if (currentDamage == 0) {
                possibleWays += countPossibleWay(index, condition, damages, current + 1, damagePosition, 0)
            }
        }
        if (condition[current] != '.' && (damagePosition < damages.size && currentDamage < damages[damagePosition])) {
            possibleWays += countPossibleWay(index, condition, damages, current + 1, damagePosition, currentDamage + 1)
        }
        countMemory[index]!![listOf(current, damagePosition, currentDamage)] = possibleWays
        return possibleWays
    }

}