fun main() {
    val input = readInput("Day08")

    checkExample1()
    part1(input).println()

    checkExample2()
    part2(input).println()
}

private fun checkExample1() {
    val example1 = listOf(
        "RL",
        " ",
        "AAA = (BBB, CCC)",
        "BBB = (DDD, EEE)",
        "CCC = (ZZZ, GGG)",
        "DDD = (DDD, DDD)",
        "EEE = (EEE, EEE)",
        "GGG = (GGG, GGG)",
        "ZZZ = (ZZZ, ZZZ)",
    )
    val expected1 = 2
    check(part1(example1) == expected1)

    val example2 = listOf(
        "LLR",
        " ",
        "AAA = (BBB, BBB)",
        "BBB = (AAA, ZZZ)",
        "ZZZ = (ZZZ, ZZZ)",
    )
    val expected2 = 6
    check(part1(example2) == expected2)
}

private fun checkExample2() {
    val example1 = listOf(
        "LR",
        " ",
        "11A = (11B, XXX)",
        "11B = (XXX, 11Z)",
        "11Z = (11B, XXX)",
        "22A = (22B, XXX)",
        "22B = (22C, 22C)",
        "22C = (22Z, 22Z)",
        "22Z = (22B, 22B)",
        "XXX = (XXX, XXX)",

    )
    val expected1 = 6L
    check(part2(example1) == expected1)
}

private fun part1(input: List<String>): Int {
    val network = Day08Network.parse(input)
    return network.getStepTo("ZZZ")
}

private fun part2(input: List<String>): Long {
    val network = Day08Network.parse(input)
    return network.getStepToMultipleCursor('Z')
}

private data class Day08Network(
    val navigator: String,
    val nodes: Map<String, Pair<String, String>>
) {
    companion object {
        fun parse(input: List<String>): Day08Network {
            val navigator = input[0]
            val nodes = mutableMapOf<String, Pair<String, String>>()
            input.drop(2).forEach {
                val key = it.substringBefore('=').trim()
                val (left, right) = it.substringAfter('(').substringBefore(')').split(", ")
                nodes[key] = Pair(left, right)
            }
            return Day08Network(navigator, nodes)
        }
    }

    fun getStepTo(destination: String): Int {
        var currentNodeName = "AAA"
        var step = 0
        while (currentNodeName != destination) {
            currentNodeName = if (navigator[step % navigator.length] == 'L') {
                nodes[currentNodeName]!!.first
            } else {
                nodes[currentNodeName]!!.second
            }
            step++
        }
        return step
    }

    fun getStepToMultipleCursor(destinationSuffix: Char): Long {
        var currentNodeNames = nodes.keys.filter { it.last() == 'A' }
        var step = 0L

        val cycles = LongArray(currentNodeNames.size) { -1 }
        while (cycles.any { it < 0 }) {
            currentNodeNames = currentNodeNames.mapIndexed { index, nodeName ->
                val next = if (navigator[step.toInt() % navigator.length] == 'L') {
                    nodes[nodeName]!!.first
                } else {
                    nodes[nodeName]!!.second
                }
                if (next.last() == destinationSuffix && cycles[index] < 0) {
                    cycles[index] = step + 1L
                }
                next
            }
            step++
        }
        return cycles.reduce { acc, n -> lcm(acc, n) }
    }
}

private fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)
private fun lcm(a: Long, b: Long): Long = (a * b) / gcd(a, b)