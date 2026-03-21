package com.mycompany.app;

import java.util.List;
import java.util.Scanner;

/**
 * Demonstrator class - Main entry point for the Catan simulator
 * Runs a sample simulation demonstrating R3.1 (undo/redo), R3.2 (Template Method), and R3.3 (Facade).
 */
public class Demonstrator {
	/**
	 * Main entry point
	 * @param args Command line arguments (unused)
	 */
	public static void main(String[] args) {
		System.out.println("===========================================");
		System.out.println("  Settlers of Catan Simulator");
		System.out.println("  SFWRENG 2AA4 - Assignment 3");
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

		System.out.println("\n===========================================");
		System.out.println("Starting simulation...");
		System.out.println("===========================================");
		System.out.println();

		// Shared scanner for step-forward and human input
		Scanner consoleScanner = new Scanner(System.in);
		Simulator sim = new Simulator(maxTurns, consoleScanner, engine -> List.of(
				new HumanPlayer(0, consoleScanner),
				new RuleBasedAgent(1, engine),
				new RandomAgent(2),
				new RandomAgent(3)
		));
		sim.runSimulation();

		System.out.println("\n===========================================");
		System.out.println("Simulation complete!");
		System.out.println("===========================================");
	}
}
