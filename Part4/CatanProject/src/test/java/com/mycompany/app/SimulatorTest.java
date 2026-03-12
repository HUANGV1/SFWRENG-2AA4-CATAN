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
     * Step-forward and game loop (Person 3): simulator must prompt for "go",
     * and with piped input complete one round without throwing. Asserts only
     * on step-forward prompt and round completion—no parser or HumanPlayer behavior.
     */
    @Test
    void testStepForwardAndRoundCompletion() {
        String input = "go\nend turn\ngo\ngo\ngo\n";
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

        assertTrue(out.contains("Type 'go' to proceed"),
                "Step-forward must prompt for 'go'; got: " + out);

        assertTrue(out.contains("End of round") || out.contains("Game over") || out.contains("Maximum rounds"),
                "Simulation must complete one round; got: " + out);
    }
}
