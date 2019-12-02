package it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics

import aima.core.search.local.Individual
import it.unibo.ai.didattica.competition.tablut.aiclient.TablutIterativeDeepeningClient
import it.unibo.ai.didattica.competition.tablut.aiclient.game.TablutGame
import it.unibo.ai.didattica.competition.tablut.client.TablutClient
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.util.orThrow

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


    private val info: Map<Heuristic, Double> = with(individual.representation) {
        mapOf(
            KingDistance() to component1(),
            KingStrategicPosition() to component2(),
            KingSurrounded() to component3(),
            PawnsDifference() to component4()
        )
    }

    val weights: List<Double> = info.map { it.value }

    val heuristic: Heuristic = WeightedHeuristic(*info.toList().toTypedArray())

    val players: Pair<TablutClient, TablutClient> = listOf("white", "black").map { role ->
        TablutIterativeDeepeningClient(
            player = role,
            name = "Genetic",
            heuristic = heuristic
        )
    }.run { component1() to component2() }

    override fun evaluate(game: TablutGame, state: State, player: State.Turn): Double =
        heuristic.evaluate(game, state, player)
}