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
		System.out.println("  SFWRENG 2AA4 - Assignment 2");
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
		System.out.println("Players: 1 HumanPlayer (undo/redo), 1 RuleBasedAgent (R3.2/R3.3), 2 RandomAgents");
		System.out.println("Win condition: 10 Victory Points");
		System.out.println("Board: Standard Catan (19 tiles, 54 nodes, 72 edges)");

		System.out.println("\n--- Game Rules (Implemented) ---");
		System.out.println("✓ Settlement building (1 LUMBER, 1 BRICK, 1 GRAIN, 1 WOOL)");
		System.out.println("✓ Road building (1 LUMBER, 1 BRICK)");
		System.out.println("✓ City upgrades (3 ORE, 2 GRAIN)");
		System.out.println("✓ Resource distribution on dice roll");
		System.out.println("✓ Distance-2 rule for settlements");
		System.out.println("✓ Road connectivity validation");
		System.out.println("✓ Victory point tracking");
		System.out.println("✓ Roll 7: no production, discard half if >7 cards, move robber and steal");

		System.out.println("\n--- Game Rules (Excluded per R1.3) ---");
		System.out.println("✗ Harbour tiles");
		System.out.println("✗ Trading (domestic and maritime)");
		System.out.println("✗ Development cards");
		System.out.println("✗ Player-chosen robber tile (random placement used on roll 7)");

		System.out.println("\n===========================================");
		System.out.println("Starting simulation...");
		System.out.println("===========================================");
		System.out.println("During Player 0 (HumanPlayer) turns: type 'undo' or 'redo' to reverse/replay build commands.");
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
