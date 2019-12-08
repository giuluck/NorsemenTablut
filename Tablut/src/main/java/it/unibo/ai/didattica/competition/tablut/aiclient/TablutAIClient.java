package it.unibo.ai.didattica.competition.tablut.aiclient;

/**
 * The entry point of the desired version of the intelligent client.
 */
public class TablutAIClient {

    public static void main(final String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java -jar TablutAIClient.jar [role] [timeout] [server]");
            return;
        }
        final String role = args[0];
        final int timeout = Integer.parseInt(args[1]);
        final String server = args[2];
        new TablutIterativeDeepeningClient(role, "Norsemen", timeout, server).run();
    }
}