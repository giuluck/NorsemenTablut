package it.unibo.ai.didattica.competition.tablut.test

import it.unibo.ai.didattica.competition.tablut.aiclient.TablutIterativeDeepeningClient
import it.unibo.ai.didattica.competition.tablut.simulation.TablutSimulation
import it.unibo.ai.didattica.competition.tablut.simulation.vs

fun main() {
    TablutSimulation().singleMatch(
        TablutIterativeDeepeningClient("white", "NorsemenWhite", 60) vs
        TablutIterativeDeepeningClient("black", "NorsemenBlack", 60)
    )
}