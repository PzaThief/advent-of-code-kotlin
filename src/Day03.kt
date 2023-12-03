fun main() {
    val input = readInput("Day03")

    checkExample1()
    part1(input).println()

    checkExample2()
    part2(input).println()
}

private fun checkExample1() {
    val example = listOf(
        "467..114..",
        "...*......",
        "..35..633.",
        "......#...",
        "617*......",
        ".....+.58.",
        "..592.....",
        "......755.",
        "...$.*....",
        ".664.598.."
    )
    val expected = 4361
    check(part1(example) == expected)
}

private fun checkExample2() {
    val example = listOf(
        "467..114..",
        "...*......",
        "..35..633.",
        "......#...",
        "617*......",
        ".....+.58.",
        "..592.....",
        "......755.",
        "...$.*....",
        ".664.598.."
    )
    val expected = 467835
    check(part2(example) == expected)
}

private fun part1(input: List<String>): Int {
    var sum = 0
    val markSet = mutableSetOf<Pair<Int, Int>>()
    for (i in input.indices) {
        for (j in input[i].indices) {
            if (input[i][j] != '.' && !input[i][j].isDigit()) {
                markSet.add(Pair(i - 1, j - 1))
                markSet.add(Pair(i - 1, j))
                markSet.add(Pair(i - 1, j + 1))
                markSet.add(Pair(i, j - 1))
                markSet.add(Pair(i, j + 1))
                markSet.add(Pair(i + 1, j - 1))
                markSet.add(Pair(i + 1, j))
                markSet.add(Pair(i + 1, j + 1))
            }
        }
    }

    for (i in input.indices) {
        var stack = ""
        var isValid = false
        for (j in input[i].indices) {
            if (input[i][j].isDigit()) {
                stack += input[i][j]
                if (markSet.contains(Pair(i, j))) {
                    isValid = true
                }
            }
            if ((!input[i][j].isDigit() && stack.isNotEmpty()) || j == input[i].lastIndex) {
                if (isValid) {
                    sum += stack.toInt()
                    isValid = false
                }
                stack = ""
            }
        }
    }


    return sum
}

private fun part2(input: List<String>): Int {
    var sum = 0
    val markMap = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()
    for (i in input.indices) {
        for (j in input[i].indices) {
            if (input[i][j] == '*') {
                val starPosition = Pair(i, j)
                markMap[Pair(i - 1, j - 1)] = starPosition
                markMap[Pair(i - 1, j)] = starPosition
                markMap[Pair(i - 1, j + 1)] = starPosition
                markMap[Pair(i, j - 1)] = starPosition
                markMap[Pair(i, j + 1)] = starPosition
                markMap[Pair(i + 1, j - 1)] = starPosition
                markMap[Pair(i + 1, j)] = starPosition
                markMap[Pair(i + 1, j + 1)] = starPosition
            }
        }
    }

    val numberByStar = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()
    for (i in input.indices) {
        var stack = ""
        var adjacentStar: Pair<Int, Int>? = null
        for (j in input[i].indices) {
            if (input[i][j].isDigit()) {
                stack += input[i][j]
                if (markMap.containsKey(Pair(i, j))) {
                    adjacentStar = markMap[Pair(i, j)]
                }
            }
            if ((!input[i][j].isDigit() && stack.isNotEmpty()) || j == input[i].lastIndex) {
                if (adjacentStar != null) {
                    numberByStar.compute(adjacentStar) { k, v ->
                        if (v == null) {
                            Pair(1, stack.toInt())
                        } else {
                            Pair(v.first + 1, v.second * stack.toInt())
                        }
                    }
                    adjacentStar = null
                }
                stack = ""
            }
        }
    }

    for (possibleNumber in numberByStar.values) {
        if (possibleNumber.first == 2) {
            sum += possibleNumber.second
        }
    }

    return sum
}
