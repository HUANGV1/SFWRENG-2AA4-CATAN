package catan;

import catan.simulator.Simulator;

/**
 * Main application entry point for the Catan simulator.
 */
public class App {
    public static void main(String[] args) {
        System.out.println("=== Settlers of Catan Simulator ===\n");

        int maxRounds = 8192;
        if (args.length > 0) {
            maxRounds = ConfigParser.readMaxTurns(args[0]);
        }

        Simulator simulator = new Simulator(maxRounds);
        simulator.runSimulation();
    }
}
