import java.util.*

fun main() {
    val input = readInput("Day20")

    checkExample1()
    part1(input).println()

//    checkExample2()
//    part2(input).println()
}

private fun checkExample1() {
    var example = listOf(
        "broadcaster -> a, b, c",
        "%a -> b",
        "%b -> c",
        "%c -> inv",
        "&inv -> a",
    )
    var expected = 32000000L
    check(part1(example) == expected)

    example = listOf(
        "broadcaster -> a",
        "%a -> inv, con",
        "&inv -> b",
        "%b -> con",
        "&con -> output",
    )
    expected = 11687500L
    check(part1(example) == expected)
}

private fun checkExample2() {
}

private fun part1(input: List<String>): Long {
    val system = Day20System.parse(input)
    return system.outputPulse()
}

private fun part2(input: List<String>): Long {
    return 0
}

private class Day20System(val broadcaster: List<String>, val modules: Map<String, Module>) {
    data class Module(var name: String, var type: Type, val outputs: List<String>) {
        enum class Type {
            FlipFlop,
            Conjunction;

            companion object {
                fun from(str: Char) = if (str == '%') FlipFlop else Conjunction
            }
        }

        companion object {
            fun parse(input: String): Pair<String, Module> {
                val (moduleStr, outputStr) = input.split(" -> ")
                val type = Type.from(moduleStr.first())
                val name = moduleStr.drop(1)
                val outputs = outputStr.split(", ")
                return name to Module(name, type, outputs)
            }
        }

        var memory: MutableMap<String, Boolean> = mutableMapOf()
        var on = false

        fun addInput(input: String) {
            memory[input] = PULSE_LOW
        }

        fun route(signal: Signal): Signal? {
            return if (type == Type.FlipFlop) {
                if (signal.pulse == PULSE_HIGH) return null
                on = !on
                Signal(on, name, outputs)
            } else {
                memory[signal.from] = signal.pulse
                val outputPulse = if (memory.values.all { it == PULSE_HIGH }) PULSE_LOW else PULSE_HIGH
                Signal(outputPulse, name, outputs)
            }
        }
    }

    data class Signal(val pulse: Boolean, val from: String,  val to:List<String>)

    companion object {
        const val PULSE_HIGH = true
        const val PULSE_LOW = false

        fun parse(input: List<String>): Day20System {
            println(input)
            val broadcasterIndex = input.indexOfFirst { it.startsWith("broadcaster") }
            val broadcaster = input[broadcasterIndex].substringAfter("-> ").split(", ")
            val modules = input.filterIndexed { i, _ -> i != broadcasterIndex }.associate {
                Module.parse(it)
            }
            modules.forEach { (k, v) ->
                for (output in v.outputs) {
                    if (modules[output] != null) modules[output]!!.addInput(k)
                }
            }

            return Day20System(broadcaster, modules)
        }
    }

    fun outputPulse(pulse: Boolean = PULSE_LOW, times: Int = 1000): Long {
        val highPulses = linkedMapOf<Int, Long>()
        val lowPulses = linkedMapOf<Int, Long>()
        var attempt = 0
        while (attempt < times) {
            var highPulse = 0L
            var lowPulse = 0L
            if (pulse == PULSE_HIGH) highPulse++ else lowPulse++
            val queue = LinkedList<Signal>()
//            val path = mutableListOf<Signal>()
            queue.add(Signal(pulse, "button", broadcaster))
            while (queue.isNotEmpty()) {
                val signal = queue.pop()
                for (input in signal.to) {
                    if (signal.pulse == PULSE_HIGH) highPulse++
                    else lowPulse++

                    if (!modules.contains(input)) break
                    val next = modules[input]!!.route(signal)
                    if (next != null) queue.add(next)
                }
//                path.add(signal)
            }
            attempt++
            if (highPulse != 0L) highPulses.compute(attempt) { _, v -> if (v == null) highPulse else v + highPulse }
            if (lowPulse != 0L) lowPulses.compute(attempt) { _, v -> if (v == null) lowPulse else v + lowPulse }
        }

        return highPulses.values.sum() * lowPulses.values.sum()
    }
}