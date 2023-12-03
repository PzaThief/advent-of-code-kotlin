fun main() {
    val input = readInput("Day03")

    checkExample1()
    part1(input).println()

//    checkExample2()
//    part2(input).println()
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
    val expected = 4361
    check(part2(example) == expected)
}

private fun part1(input: List<String>): Int {
    var sum = 0
    val markSet = mutableSetOf<Pair<Int, Int>>()
    for (i in input.indices) {
        for (j in input[i].indices) {
            if (input[i][j] != '.' && !input[i][j].isDigit()) {
                markSet.add(Pair(i-1, j-1))
                markSet.add(Pair(i-1, j))
                markSet.add(Pair(i-1, j+1))
                markSet.add(Pair(i, j-1))
//                markSet.add(Pair(i, j)) // unnecessary
                markSet.add(Pair(i, j+1))
                markSet.add(Pair(i+1, j-1))
                markSet.add(Pair(i+1, j))
                markSet.add(Pair(i+1, j+1))
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
            if ((!input[i][j].isDigit() && stack.isNotEmpty()) || j == input[i].lastIndex){
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
    return 0
}
