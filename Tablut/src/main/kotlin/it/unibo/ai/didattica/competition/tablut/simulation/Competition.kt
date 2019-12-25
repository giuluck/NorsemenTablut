package it.unibo.ai.didattica.competition.tablut.simulation

import it.unibo.ai.didattica.competition.tablut.aiclient.colish.TablutColishClient
import it.unibo.ai.didattica.competition.tablut.aiclient.norsemen.TablutNorsemenClient
import it.unibo.ai.didattica.competition.tablut.aiclient.picklericks.TablutPickleRicksClient
import it.unibo.ai.didattica.competition.tablut.aiclient.ragnarock.TablutRagnarockClient
import it.unibo.ai.didattica.competition.tablut.util.toFile

fun main(args: Array<String>): Unit = with(TablutSimulation(args.orDefault()).championship(
    listOf("black", "white").flatMap {
        listOf(
            TablutColishClient(it, args.orDefault()),
            TablutNorsemenClient(it, timeout = args.orDefault()),
            TablutPickleRicksClient(it, args.orDefault()),
            TablutRagnarockClient(it, args.orDefault())
        )
    }
)) {
    saveToFile()
    val matches: Map<String, Int> = matches().withoutRole { sum() }
    val endings: Map<String, Triple<Double, Double, Double>> = endings().withoutRole { reduce { acc, triple -> acc + triple } }
    val ranking: Map<String, Double> = endings.mapValues { (_, matches) -> 3 * matches.first + matches.second }
    val averageMoves: Map<String, Double> = averageMoves().withoutRole { average() }

    StringBuilder().append(String.format("%-2s %-12s %-4s %-3s %-3s %-3s %-3s %-5s", "#", "Team Name", "Pt", "W", "D", "L", "M", "Avg"))
        .append("\n------------------------------------------\n")
        .append(ranking.entries.sortedByDescending { it.value }.mapIndexed { index, entry ->
            String.format(
                "%-2d %-12s %-4d %-3d %-3d %-3d %-3d %-5.2f",
                index + 1, // position
                entry.key, // team name
                entry.value, // points
                endings[entry.key]?.first, // wins
                endings[entry.key]?.second, // draws
                endings[entry.key]?.third, // losses
                matches[entry.key], // matches
                averageMoves[entry.key] // average moves
            )
        }.joinToString(separator = "\n"))
        .toString().toFile("benchmarks/ranking.txt")
}

private fun Array<String>.orDefault(): Int = if (size > 0) this[0].toInt() else 60

private operator fun Triple<Double, Double, Double>.plus(other: Triple<Double, Double, Double>): Triple<Double, Double, Double> =
    Triple(first + other.first, second + other.second, third + other.third)