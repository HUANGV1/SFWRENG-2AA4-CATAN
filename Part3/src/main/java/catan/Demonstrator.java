package catan;

import catan.simulator.Simulator;

/**
 * Demonstrator entry point that runs a verbose simulation.
 */
public class Demonstrator {
    public static void main(String[] args) {
        System.out.println("=== Settlers of Catan Simulator - Demonstration ===\n");

        int maxRounds = 500;
        if (args.length > 0) {
            try {
                maxRounds = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid argument, using default max rounds: " + maxRounds);
            }
        }

        Simulator simulator = new Simulator(maxRounds, 42L);
        simulator.runSimulation();
    }
}
