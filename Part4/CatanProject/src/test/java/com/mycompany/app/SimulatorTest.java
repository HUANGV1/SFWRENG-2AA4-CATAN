package com.mycompany.app;

import org.junit.jupiter.api.Test;
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
}
