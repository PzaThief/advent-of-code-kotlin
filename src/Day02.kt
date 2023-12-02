fun main() {
    val input = readInput("Day02")
    val givenCubeSet = Game.CubeSet(12, 13, 14)

    checkExample1()
    part1(givenCubeSet, input).println()
}

private fun checkExample1() {
    val example = listOf(
        "Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green",
        "Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue",
        "Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red",
        "Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red",
        "Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green"
    )
    val givenCubeSet = Game.CubeSet(12, 13, 14)
    val expected = 1 + 2 + 5
    check(part1(givenCubeSet, example) == expected)
}

private fun part1(givenCubeSet: Game.CubeSet, input: List<String>): Int {
    val games = Game.parse(input)
    return games.sumOf {
        val maximumCubes = it.getMaximumCubes()
        if (maximumCubes.red <= givenCubeSet.red && maximumCubes.green <= givenCubeSet.green && maximumCubes.blue <= givenCubeSet.blue) {
            it.id
        } else {
            0
        }
    }
}

private data class Game(
    val id: Int,
    val sets: List<CubeSet>
) {
    data class CubeSet(
        val red: Int,
        val green: Int,
        val blue: Int
    ) {
        companion object {
            fun parse(input: String): CubeSet {
                var (red, green, blue) = listOf(0, 0, 0)
                input.split(CUBE_DELIMITER).forEach { cubeStr ->
                    val (num, color) = cubeStr.trim().split(" ")
                    when (color) {
                        "red" -> red = num.toInt()
                        "green" -> green = num.toInt()
                        "blue" -> blue = num.toInt()
                    }
                }
                return CubeSet(red, green, blue)
            }
        }
    }

    companion object {
        const val GAME_DELIMITER = ":"
        const val GAME_SET_DELIMITER = ";"
        const val CUBE_DELIMITER = ","
        fun parse(input: List<String>): List<Game> {
            return input.map {
                parse(it)
            }
        }

        fun parse(input: String): Game {
            val id = input.substringBefore(GAME_DELIMITER).substring("Game ".length).toInt()
            val cubeSets = input.substringAfter(GAME_DELIMITER).split(GAME_SET_DELIMITER).map {
                CubeSet.parse(it)
            }
            return Game(id, cubeSets)
        }
    }

    fun getMaximumCubes(): CubeSet {
        var (red, green, blue) = listOf(0, 0, 0)
        for (cube in this.sets) {
            red = maxOf(red, cube.red)
            green = maxOf(green, cube.green)
            blue = maxOf(blue, cube.blue)
        }
        return CubeSet(red, green, blue)
    }
}
