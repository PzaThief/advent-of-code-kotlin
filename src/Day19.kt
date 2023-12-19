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
    return system.countDistinctCombinations()
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

    fun countDistinctCombinations(range: IntRange = 1..4000): Long {
        return 0L

    }
}