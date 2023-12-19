import java.util.*
import kotlin.math.max
import kotlin.math.min

fun main() {
    val input = readInput("Day19")

    checkExample1()
    part1(input).println()

    checkExample2()
    part2(input).println()
}

private fun checkExample1() {
    val example = listOf(
        "px{a<2006:qkq,m>2090:A,rfg}",
        "pv{a>1716:R,A}",
        "lnx{m>1548:A,A}",
        "rfg{s<537:gd,x>2440:R,A}",
        "qs{s>3448:A,lnx}",
        "qkq{x<1416:A,crn}",
        "crn{x>2662:A,R}",
        "in{s<1351:px,qqz}",
        "qqz{s>2770:qs,m<1801:hdj,R}",
        "gd{a>3333:R,R}",
        "hdj{m>838:A,pv}",
        "",
        "{x=787,m=2655,a=1222,s=2876}",
        "{x=1679,m=44,a=2067,s=496}",
        "{x=2036,m=264,a=79,s=2244}",
        "{x=2461,m=1339,a=466,s=291}",
        "{x=2127,m=1623,a=2188,s=1013}",
    )
    val expected = 19114L
    check(part1(example) == expected)
}

private fun checkExample2() {
    val example = listOf(
        "px{a<2006:qkq,m>2090:A,rfg}",
        "pv{a>1716:R,A}",
        "lnx{m>1548:A,A}",
        "rfg{s<537:gd,x>2440:R,A}",
        "qs{s>3448:A,lnx}",
        "qkq{x<1416:A,crn}",
        "crn{x>2662:A,R}",
        "in{s<1351:px,qqz}",
        "qqz{s>2770:qs,m<1801:hdj,R}",
        "gd{a>3333:R,R}",
        "hdj{m>838:A,pv}",
        "",
        "{x=787,m=2655,a=1222,s=2876}",
        "{x=1679,m=44,a=2067,s=496}",
        "{x=2036,m=264,a=79,s=2244}",
        "{x=2461,m=1339,a=466,s=291}",
        "{x=2127,m=1623,a=2188,s=1013}",
    )
    val expected = 167409079868000L
    check(part2(example) == expected)
}

private fun part1(input: List<String>): Long {
    val system = Day19System.parse(input)
    return system.sumAcceptedPartsRatings()
}

private fun part2(input: List<String>): Long {
    val system = Day19System.parse(input)
    return system.countAcceptedCombinations()
}

private class Day19System(val workflows: Map<String, Workflow>, val parts: List<Map<Char, Int>>) {
    data class Workflow(val rules: List<Rule>, val default: Rating) {
        data class Rule(val category: Char, val range: IntRange, val rating: Rating)
        data class Rating(val accept: Boolean? = null, val next: String? = null) {
            companion object {
                fun parse(input: String): Rating {
                    return when (input) {
                        "A" -> Rating(accept = true)
                        "R" -> Rating(accept = false)
                        else -> Rating(next = input)
                    }
                }
            }
        }

        companion object {
            fun parse(input: String): Workflow {
                val rules = mutableListOf<Rule>()
                val str = input.removeSurrounding("{", "}")
                val ruleStrings = str.split(',')
                ruleStrings.dropLast(1).forEach {
                    val category = it.first()
                    val criteria = it.drop(2).substringBefore(':').toInt()
                    val range = if (it[1] == '>') {
                        criteria + 1..Int.MAX_VALUE
                    } else {
                        Int.MIN_VALUE..<criteria
                    }
                    rules.add(Rule(category, range, Rating.parse(it.substringAfter(':'))))
                }
                val default = Rating.parse(ruleStrings.last())
                return Workflow(rules, default)
            }
        }
    }

    companion object {
        fun parse(input: List<String>): Day19System {
            val workflow = buildMap {
                input.takeWhile { it.isNotBlank() }.forEach {
                    val workflowName = it.substringBefore('{')
                    val workflow = Workflow.parse(it.substring(workflowName.length))
                    put(workflowName, workflow)
                }
            }
            val parts = input.takeLastWhile { it.isNotBlank() }.map {
                buildMap {
                    it.removeSurrounding("{", "}").split(',').map {
                        put(it.first(), it.substringAfter('=').toInt())
                    }
                }
            }
            return Day19System(workflow, parts)
        }

        const val START_WORKFLOW = "in"
    }

    fun acceptedParts(): List<Map<Char, Int>> {
        return parts.filter { part ->
            var currentWorkflow: Workflow
            var rating = Workflow.Rating(next = START_WORKFLOW)
            var ratingChain = ""
            while (rating.accept == null) {
                ratingChain = "$ratingChain -> ${rating.next}"
                currentWorkflow = workflows[rating.next!!]!!
                val validRule = currentWorkflow.rules.firstOrNull {
                    part[it.category] in it.range
                }
                rating = validRule?.rating ?: currentWorkflow.default
            }
            rating.accept!!
        }
    }

    fun sumAcceptedPartsRatings() = acceptedParts().sumOf { it.values.sum().toLong() }

    fun countAcceptedCombinations(range: IntRange = 1..4000): Long {
        var combinations = 0L
        val queue = LinkedList<Pair<Map<Char, List<IntRange>>, Workflow>>()
        queue.offer(
            mapOf(
                'x' to listOf(range),
                'm' to listOf(range),
                'a' to listOf(range),
                's' to listOf(range)
            ) to workflows[START_WORKFLOW]!!
        )
        while (queue.isNotEmpty()) {
            val partAndWorkflow = queue.pop()
            val part = partAndWorkflow.first.toMutableMap()
            partAndWorkflow.second.rules.forEach { rule ->
                val queueRange = part[rule.category]!!.toMutableList()
                val processedRanges = mutableSetOf<IntRange>()
                var index = 0
                while (queueRange.lastIndex >= index) {
                    val categoryRange = queueRange[index]
                    index++

                    val intersectRange = max(categoryRange.first, rule.range.first)..min(categoryRange.last, rule.range.last)
                    if (intersectRange.isEmpty()) continue
                    processedRanges.add(categoryRange)

                    val leftSide = categoryRange.first..<intersectRange.first
                    if (!leftSide.isEmpty()) queueRange.add(leftSide)
                    val rightSide = intersectRange.last + 1..categoryRange.last
                    if (!rightSide.isEmpty()) queueRange.add(rightSide)

                    if (rule.rating.accept == false) continue
                    if (rule.rating.accept == true) {
                        combinations += part.entries.fold(1L) { acc, it ->
                            acc * if (it.key == rule.category) (intersectRange.last - intersectRange.first + 1)
                            else it.value.sumOf { range -> range.last - range.first + 1 }
                        }
                        continue
                    }

                    val nextPart = part.mapValues {
                        if (it.key != rule.category) it.value
                        else listOf(intersectRange)
                    }
                    queue.offer(nextPart to workflows[rule.rating.next]!!)
                }
                part[rule.category] = queueRange.filter { it !in processedRanges }
            }
            if (partAndWorkflow.second.default.accept == true) {
                combinations += part.entries.fold(1L) { acc, it -> acc * it.value.sumOf { range -> range.last - range.first + 1 } }
            } else if (partAndWorkflow.second.default.next != null) {
                queue.offer(part to workflows[partAndWorkflow.second.default.next]!!)
            }
        }

        return combinations
    }
}