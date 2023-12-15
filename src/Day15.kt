fun main() {
    val input = readInput("Day15")

    checkExample1()
    part1(input).println()

    checkExample2()
    part2(input).println()
}

private fun checkExample1() {
    val example = listOf(
        "rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7",
    )
    val expected = 1320L
    check(part1(example) == expected)
}

private fun checkExample2() {
    val example = listOf(
        "rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7",
    )
    val expected = 145L
    check(part2(example) == expected)
}

private fun part1(input: List<String>): Long {
    val sequence = input.first().split(',')
    return sequence.sumOf { hash(it).toLong() }
}

private fun part2(input: List<String>): Long {
    val sequence = input.first().split(',')
    val lenses = LinkedHashMap<String, Pair<Int, Int>>()
    sequence.forEach {
        if (it.last() == '-') {
            val label = it.dropLast(1)
            lenses.remove(label)
        } else {
            val label = it.dropLast(2)
            val box = hash(label)
            val focalLength = it.last().digitToInt()
            lenses.compute(label) { _, lens ->
                Pair(lens?.first ?: box, focalLength)
            }
        }
    }
    val slotByBox = mutableMapOf<Int, Int>()
    return lenses.values.sumOf {
        val slot = slotByBox.compute(it.first) { _, slot ->
            if (slot == null) 1 else slot + 1
        }!!
        (it.first + 1) * slot * it.second.toLong()
    }
}

private fun hash(str: String) = str.fold(0) { acc, c -> (acc + c.code) * 17 % 256 }
