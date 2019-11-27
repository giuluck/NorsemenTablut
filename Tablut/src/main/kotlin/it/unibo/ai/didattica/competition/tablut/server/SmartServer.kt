package it.unibo.ai.didattica.competition.tablut.server

import com.google.gson.Gson
import it.unibo.ai.didattica.competition.tablut.domain.*
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn
import it.unibo.ai.didattica.competition.tablut.gui.Gui
import it.unibo.ai.didattica.competition.tablut.util.StreamUtils
import it.unibo.ai.didattica.competition.tablut.util.forEachSelf
import it.unibo.ai.didattica.competition.tablut.util.tryOrPrint

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket

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

    private var gsonString: String = ""
    private val white: Client = Client(5800)
    private val black: Client = Client(5801)

    var state: State = StateTablut().apply { turn = Turn.WHITE }

    private val game: Game by lazy {
        GameAshtonTablut(
            state,
            0,
            moveCache,
            "logs",
            white.name,
            black.name
        )
    }

    private val gui: Gui by lazy {
        Gui(4)
    }

    override fun run() {
        lateinit var tcpInput: TCPInput
        var endgame = false

        // open sockets and read name
        tryOrPrint {
            listOf(white, black).forEachSelf {
                socket = ServerSocket(port)
                socket.accept().let { socket ->
                    move = DataInputStream(socket.getInputStream())
                    state = DataOutputStream(socket.getOutputStream())
                    turn = TCPInput(move)
                    turn.timer()
                    name = GSON.fromJson(gsonString, String::class.java)
                }
            }
        }

        tcpInput = white.turn
        tryOrPrint { update() }

        // game cycle
        while (!endgame) {
            tcpInput.timer()

            tryOrPrint {
                state = game.checkMove(state, GSON.fromJson(gsonString, Action::class.java))
                update()
            }

            when (state.turn) {
                Turn.WHITE -> tcpInput = white.turn
                Turn.BLACK -> tcpInput = black.turn
                else -> endgame = true
            }
        }

        // end
        tryOrPrint {
            black.socket.close()
            white.socket.close()
        }
    }

    private fun update() {
        gsonString = GSON.toJson(state)
        StreamUtils.writeString(white.state, gsonString)
        StreamUtils.writeString(black.state, gsonString)
        if (enableGui) gui.update(state)
    }

    private inner class TCPInput(private val stream: DataInputStream) : Runnable {
        override fun run(): Unit = tryOrPrint {
            gsonString = StreamUtils.readString(stream)
        }

        fun timer() = with(Thread(this)) {
            start()
            tryOrPrint {
                var counter = 0
                while (counter < time && isAlive) {
                    Thread.sleep(1000)
                    counter++
                }
            }
        }
    }

    private inner class Client(val port: Int) {
        lateinit var name: String
        lateinit var socket: ServerSocket
        lateinit var move: DataInputStream
        lateinit var state: DataOutputStream
        lateinit var turn: TCPInput
    }
}

fun main(): Unit = SmartServer().run()