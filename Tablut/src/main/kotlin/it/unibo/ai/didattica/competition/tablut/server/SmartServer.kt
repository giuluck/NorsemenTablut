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
 * A server which keeps trace of the statistics of the match.
 */
class SmartServer(private val moveTimeout: Int = 60) : Runnable {

    companion object {
        private val GSON = Gson()
    }

    var enableGui: Boolean = false

    var moves: Int = 0
    val state: State = StateTablut().apply { turn = WHITE }

    private val game: Game by lazy {
        GameAshtonTablut(state, 0, -1, "logs", white.name, black.name)
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
            repeat(moveTimeout) {
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
}

fun main(): Unit = SmartServer().run()