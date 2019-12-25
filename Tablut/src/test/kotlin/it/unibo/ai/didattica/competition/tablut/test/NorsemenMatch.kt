package it.unibo.ai.didattica.competition.tablut.test

import it.unibo.ai.didattica.competition.tablut.aiclient.norsemen.TablutNorsemenClient
import it.unibo.ai.didattica.competition.tablut.simulation.TablutSimulation
import it.unibo.ai.didattica.competition.tablut.simulation.vs

fun main() {
    TablutSimulation().singleMatch(
        TablutNorsemenClient("white", "NorsemenWhite", 60) vs
        TablutNorsemenClient("black", "NorsemenBlack", 60)
    )
}