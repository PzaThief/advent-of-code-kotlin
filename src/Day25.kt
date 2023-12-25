fun main() {
    val input = readInput("Day25")

    checkExample1()
    part1(input).println()

//    checkExample2()
//    part2(input).println()
}

private fun checkExample1() {
    val example = listOf(
        "jqt: rhn xhk nvd",
        "rsh: frs pzl lsr",
        "xhk: hfx",
        "cmg: qnr nvd lhk bvb",
        "rhn: xhk bvb hfx",
        "bvb: xhk hfx",
        "pzl: lsr hfx nvd",
        "qnr: nvd",
        "ntq: jqt hfx bvb xhk",
        "nvd: lhk",
        "lsr: lhk",
        "rzs: qnr cmg lsr rsh",
        "frs: qnr lhk lsr",
    )
    val expected = 54L
    check(part1(example) == expected)
}

private fun checkExample2() {
}

private fun part1(input: List<String>): Long {
    val network = Day25Network.parse(input)
    return network.productOfCluster()
}

private fun part2(input: List<String>): Long {
    return 0
}

private class Day25Network(val nodeMap: Map<String, Set<String>>) {
    companion object {
        fun parse(input: List<String>): Day25Network {
            val nodeMap = mutableMapOf<String, MutableSet<String>>()
            for (str in input) {
                val (left, rightStr) = str.split(": ")
                for (right in rightStr.split(" ")) {
                    nodeMap.getOrPut(left) { mutableSetOf() }.add(right)
                    nodeMap.getOrPut(right) { mutableSetOf() }.add(left)
                }
            }
            return Day25Network(nodeMap)
        }
    }

    fun productOfCluster(): Long {
        var clusterASize = nodeMap.size
        val counter = countAllNode().entries.sortedByDescending { it.value }.take(10)
        mainLoop@for (i in counter.indices) {
            for (j in i + 1 until counter.size) {
                for (k in j + 1 until counter.size) {
                    val newGraph = excludeNodes(counter[i].key, counter[j].key, counter[k].key)
                    clusterASize = counterFromStart(newGraph.keys.first(), newGraph)
                    if (clusterASize < nodeMap.size) {
                        break@mainLoop
                    }
                }
            }
        }

        return clusterASize.toLong() * (nodeMap.size - clusterASize)
    }

    private fun countAllNode(): Map<Set<String>, Int> {
        val counter = mutableMapOf<Set<String>, Int>()
        for (node in nodeMap) {
            counterFromStart(node.key, counter = counter)
        }
        return counter
    }

    private fun counterFromStart(
        start: String,
        graph: Map<String, Set<String>> = nodeMap,
        counter: MutableMap<Set<String>, Int>? = null
    ): Int {
        val seen = mutableSetOf<String>()
        val pending = ArrayDeque<String>()
        pending += start
        seen += start
        while (pending.isNotEmpty()) {
            val current = pending.removeFirst()
            val edges = graph[current]!!
            for (e in edges) {
                if (e in seen) continue
                seen += e
                counter?.merge(setOf(current, e), 1, Int::plus)
                pending += e
            }
        }
        return seen.size
    }

    private fun excludeNodes(vararg edges: Set<String>): Map<String, Set<String>> {
        return nodeMap.mapValues { (k, v) ->
            val edge = edges.firstOrNull { k in it }
            if (edge == null) {
                v
            } else {
                v - (edge - k)
            }
        }
    }

}