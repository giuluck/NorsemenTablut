package it.unibo.ai.didattica.competition.tablut.test

import io.kotlintest.TestCase
import io.kotlintest.matchers.doubles.ToleranceMatcher
import io.kotlintest.matchers.doubles.plusOrMinus
import io.kotlintest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotlintest.matchers.doubles.shouldBeLessThanOrEqual
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics.*
import it.unibo.ai.didattica.competition.tablut.game.AshtonTablut
import it.unibo.ai.didattica.competition.tablut.game.TablutGame
import it.unibo.ai.didattica.competition.tablut.game.board.allLegalMoves
import it.unibo.ai.didattica.competition.tablut.domain.Game
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut
import kotlin.random.Random

val KING_DISTANCE_HEAT_MATRIX = arrayOf(
    arrayOf(2 over 3, 1 over 1, 1 over 1, 2 over 3, 1 over 3, 2 over 3, 1 over 1, 1 over 1, 2 over 3),
    arrayOf(1 over 1, 2 over 3, 2 over 3, 1 over 3, 0 over 1, 1 over 3, 2 over 3, 2 over 3, 1 over 1),
    arrayOf(1 over 1, 2 over 3, 1 over 3, 0 over 1, -1 over 3, 0 over 1, 1 over 3, 2 over 3, 1 over 1),
    arrayOf(2 over 3, 1 over 3, 0 over 1, -1 over 3, -2 over 3, -1 over 3, 0 over 1, 1 over 3, 2 over 3),
    arrayOf(1 over 3, 0 over 1, -1 over 3, -2 over 3, -1 over 1, -2 over 3, -1 over 3, 0 over 1, 1 over 3),
    arrayOf(2 over 3, 1 over 3, 0 over 1, -1 over 3, -2 over 3, -1 over 3, 0 over 1, 1 over 3, 2 over 3),
    arrayOf(1 over 1, 2 over 3, 1 over 3, 0 over 1, -1 over 3, 0 over 1, 1 over 3, 2 over 3, 1 over 1),
    arrayOf(1 over 1, 2 over 3, 2 over 3, 1 over 3, 0 over 1, 1 over 3, 2 over 3, 2 over 3, 1 over 1),
    arrayOf(2 over 3, 1 over 1, 1 over 1, 2 over 3, 1 over 3, 2 over 3, 1 over 1, 1 over 1, 2 over 3)
)

val KING_STRATEGIC_POSITION_HEAT_MATRIX = arrayOf(
    arrayOf(3 over 1, 3 over 2, 3 over 2, 1 over 1, 1 over 1, 1 over 1, 3 over 2, 3 over 2, 3 over 1),
    arrayOf(3 over 2, 1 over 1, 1 over 1, 0 over 1, 0 over 1, 0 over 1, 1 over 1, 1 over 1, 3 over 2),
    arrayOf(3 over 2, 1 over 1, 1 over 1, 0 over 1, 0 over 1, 0 over 1, 1 over 1, 1 over 1, 3 over 2),
    arrayOf(1 over 1, 0 over 1, 0 over 1, -1 over 1, -1 over 1, -1 over 1, 0 over 1, 0 over 1, 1 over 1),
    arrayOf(1 over 1, 0 over 1, 0 over 1, -1 over 1, -1 over 1, -1 over 1, 0 over 1, 0 over 1, 1 over 1),
    arrayOf(1 over 1, 0 over 1, 0 over 1, -1 over 1, -1 over 1, -1 over 1, 0 over 1, 0 over 1, 1 over 1),
    arrayOf(3 over 2, 1 over 1, 1 over 1, 0 over 1, 0 over 1, 0 over 1, 1 over 1, 1 over 1, 3 over 2),
    arrayOf(3 over 2, 1 over 1, 1 over 1, 0 over 1, 0 over 1, 0 over 1, 1 over 1, 1 over 1, 3 over 2),
    arrayOf(3 over 1, 3 over 2, 3 over 2, 1 over 1, 1 over 1, 1 over 1, 3 over 2, 3 over 2, 3 over 1)
)

val PAWNS_DIFFERENCE_VALUES = arrayOf(
    0 over 1,
    0 over 1,
    -1 over 29,
    -1 over 29,
    1 over 27,
    -1 over 25,
    0 over 1,
    0 over 1,
    1 over 19,
    1 over 19
)

val KING_SURROUNDED_VALUES = arrayOf(
    13 over 14,
    1 over 7,
    0 over 1,
    -1 over 14,
    1 over 2,
    0 over 1,
    -1 over 2,
    -1 over 2,
    -3 over 7,
    -2 over 7
)

infix fun Int.over(denominator: Int): ToleranceMatcher = (1.0 * this / denominator) plusOrMinus 0.0001

class HeuristicsTest : StringSpec() {
    private lateinit var generator: Random
    private lateinit var referee: Game
    private lateinit var state: State
    private lateinit var game: TablutGame

    init {
        "test king distance heuristic" {
            Array(9) { Array(9) { 0.0 } }.also { heatMatrix ->
                KingDistance().apply { evaluate(game, state) }.heatMap.forEach { (coord, value) -> heatMatrix[coord.y][coord.x] = value }
            }.forEachIndexed { i, row ->
                row.forEachIndexed { j, element ->
                    element shouldBe KING_DISTANCE_HEAT_MATRIX[i][j]
                }
            }
        }

        "test king strategic position" {
            Array(9) { Array(9) { 0.0 } }.also { heatMatrix ->
                KingStrategicPosition().apply { evaluate(game, state) }.heatMap.forEach { (coord, value) -> heatMatrix[coord.y][coord.x] = value }
            }.forEachIndexed { i, row ->
                row.forEachIndexed { j, element ->
                    element shouldBe KING_STRATEGIC_POSITION_HEAT_MATRIX[i][j]
                }
            }
        }

        "test king surrounded" {
            with (KingSurrounded()) {
                valueAfter(0) shouldBe 1.0
                KING_SURROUNDED_VALUES.forEach {
                    valueAfter(10) shouldBe it
                }
            }
        }

        "test pawns difference" {
            with (PawnsDifference()) {
                valueAfter(0) shouldBe 0.0
                PAWNS_DIFFERENCE_VALUES.forEach { valueAfter(10) shouldBe it }
            }
        }

        "test norsemen heuristic" {
            with (NorsemenWhiteHeuristic) {
                while (!game.isTerminal(state)) {
                    valueAfter(1).apply {
                        shouldBeGreaterThanOrEqual(-1.0)
                        shouldBeLessThanOrEqual(1.0)
                    }
                }
            }
        }
    }

    private fun Heuristic.valueAfter(moves: Int = 0): Double {
        repeat(moves) { referee.checkMove(state, state.allLegalMoves(game.movementRules).random(generator)) }
        return evaluate(game, state)
    }

    override fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        generator = Random(42)
        referee = GameAshtonTablut(0, 0, "garbage", "fake", "fake")
        state = StateTablut()
        game = AshtonTablut()
    }
}