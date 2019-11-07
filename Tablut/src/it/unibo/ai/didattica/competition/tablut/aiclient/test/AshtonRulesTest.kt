package it.unibo.ai.didattica.competition.tablut.aiclient.test

import io.kotlintest.fail
import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import it.unibo.ai.didattica.competition.tablut.aiclient.board.Coord
import it.unibo.ai.didattica.competition.tablut.domain.*
import it.unibo.ai.didattica.competition.tablut.aiclient.games.AshtonTablut
import it.unibo.ai.didattica.competition.tablut.aiclient.rules.Rule
import it.unibo.ai.didattica.competition.tablut.aiclient.board.allLegalMoves
import it.unibo.ai.didattica.competition.tablut.aiclient.board.playerCoords
import it.unibo.ai.didattica.competition.tablut.aiclient.games.TablutGame
import kotlin.random.Random

val RANDOM_GENERATOR = Random(0)

class AshtonRulesTest : StringSpec({

    "" +
            "all valid moves must be potentially performed" {
        val game: Game = GameAshtonTablut(0, 0, "garbage", "fake", "fake")
        val state: State = StateTablut()
        val rules: Set<Rule> = AshtonTablut().rules
        val actions: List<Action> = state.allLegalMoves(rules)
        actions.size shouldBe 80
        actions.forEach {
            val temp = state.clone()
            try {
                game.checkMove(temp, it)
            } catch (e: Throwable) {
                fail("$it is not a valid move.")
            }
        }
    }

    "simulating a match no error must be encountered" {
        val game: Game = GameAshtonTablut(0, 0, "garbage", "fake", "fake")
        val state: State = StateTablut()
        val referee: TablutGame = AshtonTablut()
        val rules: Set<Rule> = referee.rules
        while (!referee.isTerminal(state)) {
            val rand = state.allLegalMoves(rules).random(RANDOM_GENERATOR)
            try {
                game.checkMove(state, rand)
            } catch (e: Throwable) {
                fail("$rand is not a valid move.")
            }
        }
    }
})