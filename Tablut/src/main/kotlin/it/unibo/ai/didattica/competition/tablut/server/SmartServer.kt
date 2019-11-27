package it.unibo.ai.didattica.competition.tablut.server

import com.google.gson.Gson
import it.unibo.ai.didattica.competition.tablut.domain.*
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn.*
import it.unibo.ai.didattica.competition.tablut.gui.Gui
import it.unibo.ai.didattica.competition.tablut.util.StreamUtils
import it.unibo.ai.didattica.competition.tablut.util.forEachSelf
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket
import kotlin.concurrent.thread

/**
 * A practically usable server.
 */
class SmartServer(
    private val time: Int = 5,
    private val moveCache: Int = -1,
    private val enableGui: Boolean = false
) : Runnable {

    companion object {
        private val GSON = Gson()
    }

    val stats: Stats
        get() = Stats(moves = moves, winner = when (state.turn) {
            WHITEWIN -> "white"
            BLACKWIN -> "black"
            DRAW -> "draw"
            else -> throw IllegalStateException("Match not finished yet")
        })

    private var moves: Int = 0
    private val state: State = StateTablut().apply { turn = WHITE }
    private val game: Game by lazy {
        GameAshtonTablut(state, 0, moveCache, "logs", white.name, black.name)
    }
    private val gui: Gui by lazy { Gui(4) }
    private val white: Client = Client(Server.whitePort)
    private val black: Client = Client(Server.blackPort)
    private var gson: String = ""

    override fun run() {
        // open sockets and read name
        listOf(white, black).forEachSelf {
            with (socket.accept()) {
                input = DataInputStream(getInputStream())
                output = DataOutputStream(getOutputStream())
                turn = TCPInput(input).apply { timer() }
                name = GSON.fromJson(gson, String::class.java)
            }
        }

        // game cycle
        update()
        generateSequence(white) { if (it == white) black else white }.map { it.turn }.forEach {
            moves++
            it.timer()
            game.checkMove(state, GSON.fromJson(gson, Action::class.java))
            update()
            if (state.turn != WHITE && state.turn != BLACK) {
                black.socket.close()
                white.socket.close()
                return
            }
        }
    }

    private fun update() {
        gson = GSON.toJson(state)
        StreamUtils.writeString(white.output, gson)
        StreamUtils.writeString(black.output, gson)
        if (enableGui) gui.update(state)
    }

    private inner class TCPInput(private val stream: DataInputStream) {
        fun timer(): Thread = thread { gson = StreamUtils.readString(stream) }.apply {
            repeat(time) {
                if (isAlive) Thread.sleep(1000)
                else return@apply
            }
        }
    }

    private inner class Client(port: Int) {
        val socket: ServerSocket = ServerSocket(port)
        lateinit var input: DataInputStream
        lateinit var output: DataOutputStream
        lateinit var turn: TCPInput
        lateinit var name: String
    }

    data class Stats(
        val winner: String,
        val moves: Int
    ) {
        companion object {
            val Collection<Stats>.victories: Map<String, Int>
                get() = groupBy { it.winner }.mapValues { (_, wonMatches) -> wonMatches.count() }

            fun Collection<Stats>.percentage(winner: String): Double =
                100.0 * filter { it.winner == winner }.count() / count()

            fun Collection<Stats>.printInfo(name: String): String =
                mapIndexed { i, (winner, moves) -> "${i + 1}: $winner -> $moves moves" }
                    .joinToString(separator = "\n", prefix = "$name\n")
        }
    }
}

fun main(): Unit = SmartServer().run()