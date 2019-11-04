package it.unibo.ai.didattica.competition.tablut.aiclient.rules

import it.unibo.ai.didattica.competition.tablut.aiclient.board.Coord
import it.unibo.ai.didattica.competition.tablut.domain.Action
import it.unibo.ai.didattica.competition.tablut.domain.State
import it.unibo.ai.didattica.competition.tablut.domain.State.*

enum class Rule(val checkRule: Action.(State) -> Boolean = { _ -> true }) {
    NO_MOVEMENT({
        rowFrom != rowTo || columnFrom != columnTo
    }),
    OUT_OF_BOARDS({ state ->
        0.until(state.board.size).toSet().containsAll(setOf(rowFrom, rowTo, columnFrom, columnTo))
    }),
    DIAGONAL_MOVEMENT({
        rowFrom == rowTo || columnFrom == columnTo
    }),
    PAWN_OCCUPATION({ state ->
        state.getPawn(rowTo, columnTo) == Pawn.EMPTY
    }),
    THRONE_OCCUPATION({ state ->
        state.getPawn(rowTo, columnTo) != Pawn.THRONE
    }),
    CITADEL_OCCUPATION({ state ->
        (Coord(rowFrom, columnFrom) to Coord(rowTo, columnTo))
            .takeIf { (_, to) -> state.citadels.contains(to) }
            ?.let { (from, to) -> if (!state.citadels.contains(from)) false else from.distanceTo(to) <= 2 }
            ?: true
    }),
    CLIMBING({
        Coord(rowFrom, columnFrom).coordsUntil(Coord(rowTo, columnTo)).all { coord ->
            it.getPawn(coord.x, coord.y) == Pawn.EMPTY && !it.citadels.contains(coord)
        }
    })
}

private val State.citadels: Set<Coord>
    get() = mutableSetOf<Coord>().also { citadels ->
        val lastCell = board.size - 1
        val middleCell = board.size / 2
        val range = (middleCell - 1).rangeTo(middleCell + 1)
        range.forEach {
            citadels.add(Coord(0, it))
            citadels.add(Coord(it, 0))
            citadels.add(Coord(lastCell, it))
            citadels.add(Coord(it, lastCell))
        }
        citadels.add(Coord(1, middleCell))
        citadels.add(Coord(middleCell, 1))
        citadels.add(Coord(lastCell - 1, middleCell))
        citadels.add(Coord(middleCell, lastCell - 1))
    }