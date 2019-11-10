package it.unibo.ai.didattica.competition.tablut.aiclient.test

import io.kotlintest.TestCase
import io.kotlintest.fail
import io.kotlintest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotlintest.specs.StringSpec
import it.unibo.ai.didattica.competition.tablut.aiclient.board.*
import it.unibo.ai.didattica.competition.tablut.domain.*
import it.unibo.ai.didattica.competition.tablut.aiclient.games.AshtonTablut
import it.unibo.ai.didattica.competition.tablut.aiclient.rules.Rule
import it.unibo.ai.didattica.competition.tablut.aiclient.games.TablutGame
import kotlin.random.Random

class AshtonRulesTest : StringSpec() {
    private lateinit var generator: Random
    private lateinit var game: Game
    private lateinit var state: State
    private lateinit var referee: TablutGame
    private lateinit var rules: Set<Rule>

    init {
        "each and only valid move must be potentially performed" {
            // brings the state to a random moment by performing 20 moves then checks
            repeat(20) { game.checkMove(state.clone(), state.allLegalMoves(rules).random(generator)) }
            state.allMoves.filter {
                try {
                    game.checkMove(state.clone(), it)
                    true
                } catch (e: Throwable) {
                    false
                }
            }.map {
                it.toString()
            } shouldContainExactlyInAnyOrder state.allLegalMoves(rules).map { it.toString() }
        }

        "simulating a match no error must be encountered" {
            while (!referee.isTerminal(state)) {
                state.allLegalMoves(rules).random(generator).also { move ->
                    try {
                        game.checkMove(state, move)
                    } catch (e: Throwable) {
                        state.toConsole()
                        fail("$move is not a valid move.")
                    }
                }
            }
        }
    }

    override fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        generator = Random(0)
        game = GameAshtonTablut(0, 0, "garbage", "fake", "fake")
        state = StateTablut()
        referee = AshtonTablut()
        rules = referee.rules
    }
}