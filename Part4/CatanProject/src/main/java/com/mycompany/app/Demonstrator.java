package com.mycompany.app;

/**
 * Demonstrator class - Main entry point for the Catan simulator
 * Runs a sample simulation demonstrating the game mechanics
 */
public class Demonstrator {
	/**
	 * Main entry point
	 * @param args Command line arguments (unused)
	 */
	public static void main(String[] args) {
		System.out.println("===========================================");
		System.out.println("  Settlers of Catan Simulator");
		System.out.println("  SFWRENG 2AA4 - Assignment 1");
		System.out.println("===========================================\n");

		// Read configuration
		int maxTurns = 100; // Default value
		try {
			maxTurns = ConfigParser.readMaxTurns("config.txt");
			System.out.println("Configuration loaded successfully");
		} catch (Exception e) {
			System.out.println("Could not read config.txt, using default max turns: " + maxTurns);
			System.out.println("Error: " + e.getMessage());
		}

		System.out.println("\n--- Game Configuration ---");
		System.out.println("Max turns: " + maxTurns);
		System.out.println("Players: 4 (RandomAgents)");
		System.out.println("Win condition: 10 Victory Points");
		System.out.println("Board: Standard Catan (19 tiles, 54 nodes, 72 edges)");

		System.out.println("\n===========================================");
		System.out.println("Starting simulation...");
		System.out.println("===========================================\n");

		// Create and run simulator
		Simulator sim = new Simulator(maxTurns);
		sim.runSimulation();

		System.out.println("\n===========================================");
		System.out.println("Simulation complete!");
		System.out.println("===========================================");
	}
}
