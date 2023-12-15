fun main() {
    val input = readInput("Day15")

    checkExample1()
    part1(input).println()

//    checkExample2()
//    part2(input).println()
}

private fun checkExample1() {
    val example = listOf(
        "rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7",
    )
    val expected = 1320L
    check(part1(example) == expected)
}

private fun checkExample2() {

}

private fun part1(input: List<String>): Long {
    val sequence = input.first().split(',')
    return sequence.sumOf { it.fold(0) { acc, c -> (acc + c.code) * 17 % 256 }.toLong() }
}

private fun part2(input: List<String>): Long {
    return 0
}