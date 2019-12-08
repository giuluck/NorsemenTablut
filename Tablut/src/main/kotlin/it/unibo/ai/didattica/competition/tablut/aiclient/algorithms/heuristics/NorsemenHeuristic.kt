package it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics

/**
 * The final heuristic to use for the white player.
 */
object NorsemenWhiteHeuristic : WeightedHeuristic(
    KingDistance() to 1.0,
    KingStrategicPosition() to 3.0,
    KingSurrounded() to 2.5,
    PawnsDifference() to 4.5
)

/**
 * The final heuristic to use for the black player.
 */
object NorsemenBlackHeuristic : WeightedHeuristic(
    KingDistance() to 1.0,
    KingStrategicPosition() to 2.0,
    KingSurrounded() to 3.0,
    PawnsDifference() to 5.0
)