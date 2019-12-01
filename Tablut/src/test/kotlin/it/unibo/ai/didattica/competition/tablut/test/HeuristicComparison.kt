package it.unibo.ai.didattica.competition.tablut.test

import it.unibo.ai.didattica.competition.tablut.aiclient.TablutIterativeDeepeningClient
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics.KingDistance
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics.KingStrategicPosition
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics.KingSurrounded
import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics.PawnsDifference
import it.unibo.ai.didattica.competition.tablut.client.TablutClient
import it.unibo.ai.didattica.competition.tablut.simulation.TablutSimulation
import it.unibo.ai.didattica.competition.tablut.simulation.saveToFile
import it.unibo.ai.didattica.competition.tablut.util.orThrow

fun main() {
    val players: Collection<TablutClient> = listOf(
        KingDistance(),
        KingStrategicPosition(),
        KingSurrounded(),
        PawnsDifference()
    ).flatMap { listOf("white", "black").map { role ->
        TablutIterativeDeepeningClient(
            player = role,
            name = it::class.simpleName.orThrow() + role.capitalize(),
            timeout = 60,
            heuristic = it
        )
    } }
    TablutSimulation(debugMode = true).championship(players).saveToFile()
}