package it.unibo.ai.didattica.competition.tablut.aiclient.ragnarock

import it.unibo.ai.didattica.competition.tablut.client.TablutClient
import java.io.File

class TablutRagnarockClient @JvmOverloads constructor(
    private val player: String,
    private val timeout: Int = 60,
    private val ipAddress: String = "localhost"
) : TablutClient(player, "Ragnarock", timeout, ipAddress) {

    private val packageName: String = "/src/main/java/it/unibo/ai/didattica/competition/tablut/aiclient/ragnarock"
    private val runningDir: File = File(System.getProperty("user.dir") + packageName)
    private val outputDir: File = File(System.getProperty("user.dir") + "/Executables/output.txt")

    override fun run() {
        ProcessBuilder("python3", "src/client.py", player.toLowerCase().capitalize(), timeout.toString(), ipAddress)
            .directory(runningDir)
            .start()
            // .waitFor()
    }
}