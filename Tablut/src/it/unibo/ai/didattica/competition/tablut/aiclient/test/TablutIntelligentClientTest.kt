package it.unibo.ai.didattica.competition.tablut.aiclient.test

import io.kotlintest.specs.StringSpec
import it.unibo.ai.didattica.competition.tablut.aiclient.TablutMonteCarloClient
import it.unibo.ai.didattica.competition.tablut.client.TablutClient
import it.unibo.ai.didattica.competition.tablut.client.TablutRandomClient
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.server.Server
import kotlinx.coroutines.*

class TablutIntelligentClientTest : StringSpec({
    "monte carlo white player should win against a random player" {
        match { TablutMonteCarloClient("white") vs TablutRandomClient("black") }
    }
})

private suspend fun match(players: () -> Pair<TablutClient, TablutClient>): State = withContext(Dispatchers.Default) {
    Server(60, -1, 0, 0, 4, true).apply {
        launch { run() }
        delay(1000)
        players().toList().forEach { launch { it.run() } }
    }.currentState
}

private infix fun TablutClient.vs(opponent: TablutClient): Pair<TablutClient, TablutClient> = this to opponent