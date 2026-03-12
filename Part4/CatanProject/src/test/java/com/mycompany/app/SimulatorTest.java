package com.mycompany.app;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SimulatorTest {

    /**
     * Constructor must wire up board, engine, and 4 players without throwing.
     * Only observable from outside via absence of exceptions.
     */
    @Test
    void testSimulatorInitialization() {
        assertDoesNotThrow(() -> new Simulator(10),
                "Simulator constructor must not throw for a valid maxRounds value");
    }

    /**
     * Constructing with maxRounds=1 (minimum meaningful value) must succeed.
     */
    @Test
    void testSimulatorInitializationWithOneRound() {
        assertDoesNotThrow(() -> new Simulator(1),
                "Simulator must accept maxRounds=1");
    }

    /**
     * Running a 2-round simulation must complete without throwing.
     * This exercises: initialSetup, rollDice, distributeResources,
     * player.takeTurn, victory-point check, and score printing.
     */
    @Test
    void testShortSimulationRunsWithoutException() {
        Simulator sim = new Simulator(2);
        assertDoesNotThrow(sim::runSimulation,
                "A 2-round simulation must run to completion without exceptions");
    }

    /**
     * Running with a large round count must also not throw (stress/stability check).
     */
    @Test
    void testLargeRoundCountDoesNotThrow() {
        Simulator sim = new Simulator(100);
        assertDoesNotThrow(sim::runSimulation,
                "Simulator must handle 100 rounds without error");
    }

    /**
     * Human input flow: simulator must prompt for "go", then during human turn
     * prompt with ">" and accept commands (e.g. help, end turn). Verifies that
     * after providing input, the program responds correctly (help text, turn ends).
     */
    @Test
    void testHumanInputPromptsAndResponds() {
        // One round: go (start player 0), help, end turn, then go for players 1,2,3
        String input = "go\nhelp\nend turn\ngo\ngo\ngo\n";
        Scanner scanner = new Scanner(input);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(captured));
            Simulator sim = new Simulator(1, scanner);
            sim.runSimulation();
        } finally {
            System.setOut(originalOut);
        }

        String out = captured.toString();

        // Must ask for "go" to proceed
        assertTrue(out.contains("Type 'go' to proceed"),
                "Output must prompt for 'go'; got: " + out);

        assertTrue(out.contains("Player 0's turn") || out.contains("Player 0"),
                "Output must indicate Player 0's turn; got: " + out);

        // During human turn, help command must print available commands
        assertTrue(out.contains("Available commands:"),
                "After 'help', output must show Available commands; got: " + out);

        assertTrue(out.contains("build road") || out.contains("roll") || out.contains("status"),
                "Help output must list commands; got: " + out);

        // Simulation should complete one round (max rounds reached or round summary)
        assertTrue(out.contains("End of round") || out.contains("Game over") || out.contains("Maximum rounds"),
                "Simulation should reach end of round or game over; got: " + out);
    }
}
