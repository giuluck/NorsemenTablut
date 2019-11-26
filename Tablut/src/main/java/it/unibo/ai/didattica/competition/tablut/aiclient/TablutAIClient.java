package it.unibo.ai.didattica.competition.tablut.aiclient;

import it.unibo.ai.didattica.competition.tablut.aiclient.algorithms.heuristics.PiecesDifference;
import it.unibo.ai.didattica.competition.tablut.aiclient.prototypes.TablutIterativeDeepeningClient;

/**
 * The entry point of the desired version of the intelligent client.
 */
public class TablutAIClient {

    public static void main(final String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java -jar TablutAIClient [role] [timeout] [server]");
            return;
        }
        final String role = args[0];
        final int timeout = Integer.parseInt(args[1]);
        final String server = args[2];
        new TablutIterativeDeepeningClient(role, timeout, server, new PiecesDifference()).run();
    }
}
