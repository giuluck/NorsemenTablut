package it.unibo.ai.didattica.competition.tablut.aiclient.norsemen.algorithms.heuristics

import aima.core.search.local.Individual
import it.unibo.ai.didattica.competition.tablut.aiclient.norsemen.TablutNorsemenClient
import it.unibo.ai.didattica.competition.tablut.game.TablutGame
import it.unibo.ai.didattica.competition.tablut.client.TablutClient
import it.unibo.ai.didattica.competition.tablut.domain.State

/**
 * A weighted heuristic which represents also an individual for a genetic algorithm.
 */
class GeneticHeuristic(
    val individual: Individual<Double>
) : Heuristic {
    constructor(
        kingDistance: Number = 0,
        kingStrategicPosition: Number = 0,
        kingSurrounded: Number = 0,
        pawnsDifference: Number = 0
    ): this(Individual(listOf(
        kingDistance.toDouble(),
        kingStrategicPosition.toDouble(),
        kingSurrounded.toDouble(),
        pawnsDifference.toDouble()
    )))

    private val info: Map<Heuristic, Double> = with(individual) {
        mapOf(
            KingDistance() to representation[0],
            KingStrategicPosition() to representation[1],
            KingSurrounded() to representation[2],
            PawnsDifference() to representation[3]
        )
    }

    val weights: List<Double> = info.map { it.value }

    val heuristic: Heuristic = WeightedHeuristic(*info.toList().toTypedArray())

    val whitePlayer: TablutClient = TablutNorsemenClient(
        player = "white",
        name = "Genetic",
        heuristic = heuristic
    )

    val blackPlayer: TablutClient = TablutNorsemenClient(
        player = "black",
        name = "Genetic",
        heuristic = heuristic
    )

    override fun evaluate(game: TablutGame, state: State, player: State.Turn): Double =
        heuristic.evaluate(game, state, player)

    override fun hashCode(): Int = weights.hashCode()

    override fun equals(other: Any?): Boolean =
        if (other is GeneticHeuristic) { weights == other.weights } else false
}