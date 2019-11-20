package it.unibo.ai.didattica.competition.tablut.aiclient.test

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import it.unibo.ai.didattica.competition.tablut.client.TablutClient
import it.unibo.ai.didattica.competition.tablut.server.Server
import kotlinx.coroutines.*

class TablutIntelligentClientTest : StringSpec({
    "monte carlo white player should win against a random player" {
        match { TablutTestClient("white") vs TablutTestClient("black") }.toConsole()
        1 shouldBe 1
    }
})

private fun match(players: () -> Pair<TablutClient, TablutClient>) = with(GlobalScope) {
    launch { Server(60, -1, 0, 0, 4, true).run() }
    players().let { (white, black) ->
        white.apply { launch { run() } }
        black.apply { run() }
    }
}.currentState

private infix fun TablutClient.vs(opponent: TablutClient): Pair<TablutClient, TablutClient> = this to opponent