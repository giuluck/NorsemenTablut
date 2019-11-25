package it.unibo.ai.didattica.competition.tablut.aiclient.test

import io.kotlintest.TestCase
import io.kotlintest.fail
import io.kotlintest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import it.unibo.ai.didattica.competition.tablut.domain.*
import it.unibo.ai.didattica.competition.tablut.aiclient.games.AshtonTablut
import it.unibo.ai.didattica.competition.tablut.aiclient.games.rules.MovementRule
import it.unibo.ai.didattica.competition.tablut.aiclient.games.TablutGame
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.allLegalMoves
import it.unibo.ai.didattica.competition.tablut.aiclient.games.board.allMoves
import kotlin.random.Random

class AshtonRulesTest : StringSpec() {
    private lateinit var generator: Random
    private lateinit var referee: Game
    private lateinit var state: State
    private lateinit var game: TablutGame
    private lateinit var rules: Collection<MovementRule>

    init {
        "each and only valid move must be potentially performed" {
            // brings the state to a random moment by performing 20 moves then checks
            repeat(20) { referee.checkMove(state, state.allLegalMoves(rules).random(generator)) }
            state.allMoves.filter {
                try {
                    referee.checkMove(state.clone(), it)
                    true
                } catch (e: Throwable) {
                    false
                }
            }.map { it.toString() }
            .toSet() shouldContainExactlyInAnyOrder state.allLegalMoves(rules).map { it.toString() }
        }

        "simulating a match randomly selecting a legal move no error must be encountered" {
            while (!game.isTerminal(state)) {
                state.allLegalMoves(rules).random(generator).also { move ->
                    try {
                        referee.checkMove(state, move)
                    } catch (e: Throwable) {
                        state.toConsole()
                        fail("$move is not a valid move.")
                    }
                }
            }
        }

        "running a game updating the state autonomously no error must be encountered" {
            var copy: State = state.clone()
            while (!game.isTerminal(copy)) {
                game.getActions(copy).random(generator).also { move ->
                    try {
                        copy = game.getResult(copy, move)
                        referee.checkMove(state, move)
                        copy shouldBe state
                    } catch (e: Throwable) {
                        fail("Unexpected game states:\n\n$copy\n\nshould have been\n\n$state")
                    }
                }
            }
        }
    }

    override fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        generator = Random(0)
        referee = GameAshtonTablut(0, 0, "garbage", "fake", "fake")
        state = StateTablut()
        game = AshtonTablut()
        rules = game.movementRules
    }
}